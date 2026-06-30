param(
    [string]$BaseUrl = "http://localhost:8080/api",
    [string]$Username = "teste-rag",
    [string]$Password = "123456",
    [string]$Email = "teste-rag@email.com"
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  TESTE COMPLETO DO FLUXO RAG" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Post-Json {
    param([string]$Url, [string]$Body, [string]$AuthToken, [int]$Timeout = 10)
    $tmpFile = "$env:TEMP\rag-body-$(Get-Random).json"
    Set-Content -Path $tmpFile -Value $Body -Encoding ASCII -NoNewline
    try {
        if ($AuthToken) {
            return curl.exe -s -X POST $Url -H "Content-Type: application/json" -H "Authorization: Bearer $AuthToken" -d "@$tmpFile" --max-time $Timeout
        } else {
            return curl.exe -s -X POST $Url -H "Content-Type: application/json" -d "@$tmpFile" --max-time $Timeout
        }
    } finally {
        Remove-Item $tmpFile -Force -ErrorAction SilentlyContinue
    }
}

# --- 1. HEALTH CHECK ---
Write-Host "[1/6] Health check..." -ForegroundColor Yellow
$raw = curl.exe -s "$BaseUrl/health" --max-time 5
if (-not $raw) {
    Write-Host "  ERRO - API nao esta respondendo." -ForegroundColor Red
    exit 1
}
$health = $raw | ConvertFrom-Json
Write-Host "  OK - Status: $($health.status)" -ForegroundColor Green

# --- 2. REGISTRAR ---
Write-Host "[2/6] Registrar usuario..." -ForegroundColor Yellow
$json = "{`"username`":`"$Username`",`"password`":`"$Password`",`"email`":`"$Email`"}"
$raw = Post-Json -Url "$BaseUrl/auth/register" -Body $json
$resp = $raw | ConvertFrom-Json
if ($resp.id) {
    Write-Host "  Usuario criado: $($resp.id)" -ForegroundColor Green
} elseif ($raw -match "already in use") {
    Write-Host "  Usuario ja existe, continuando..." -ForegroundColor DarkYellow
} else {
    Write-Host "  Aviso: $($resp.message)" -ForegroundColor DarkYellow
}

# --- 3. LOGIN ---
Write-Host "[3/6] Login..." -ForegroundColor Yellow
$json = "{`"username`":`"$Username`",`"password`":`"$Password`"}"
$raw = Post-Json -Url "$BaseUrl/auth/login" -Body $json
$login = $raw | ConvertFrom-Json
$token = $login.token
if (-not $token) {
    Write-Host "  ERRO: Login falhou." -ForegroundColor Red
    Write-Host "  Resposta: $raw" -ForegroundColor Red
    exit 1
}
Write-Host "  Token obtido: $($token.Substring(0, 20))..." -ForegroundColor Green

# --- 4. CRIAR ARQUIVO DE TESTE E FAZER UPLOAD ---
Write-Host "[4/6] Upload de documento..." -ForegroundColor Yellow
$tmpFile = "$env:TEMP\rag-doc-$(Get-Random).txt"
@"
Clean Architecture, criada por Robert C. Martin (Uncle Bob), e um padrao de design
de software que organiza o codigo em camadas concêntricas. As regras de negocio
(entidades de dominio) ficam no centro, sem dependencia de frameworks, banco de
dados ou interfaces de usuario. As camadas externas (adaptadores, infraestrutura)
dependem das internas, nunca o contrario.

Os principios fundamentais sao:
- Independencia de frameworks: o dominio nao importa bibliotecas externas
- Testabilidade: regras de negocio podem ser testadas sem infraestrutura
- Independencia de UI: a interface pode mudar sem afetar o dominio
- Independencia de banco: trocar de banco nao altera regras de negocio
- Independencia de agentes externos: servicos externos sao adaptaveis

A estrutura tipica tem 4 camadas:
1. Domain (centro): entidades, value objects, regras de negocio
2. Application: casos de uso, portas de entrada e saida
3. Adapter: implementacoes de interface (controladores, repositorios)
4. Infra: configuracao, seguranca, frameworks
"@ | Set-Content -Path $tmpFile -Encoding UTF8

$raw = curl.exe -s -X POST "$BaseUrl/documents" `
    -H "Authorization: Bearer $token" `
    -F "file=@$tmpFile" --max-time 120

$upload = $raw | ConvertFrom-Json
if (-not $upload.id) {
    Write-Host "  ERRO no upload: $raw" -ForegroundColor Red
    Remove-Item $tmpFile -Force -ErrorAction SilentlyContinue
    exit 1
}

$docId = $upload.id
Write-Host "  Documento enviado: $docId (status: $($upload.status))" -ForegroundColor Green

# --- 5. AGUARDAR PROCESSAMENTO ---
Write-Host "[5/6] Aguardando processamento do documento..." -ForegroundColor Yellow
$maxWait = 60
$elapsed = 0
do {
    Start-Sleep -Seconds 3
    $elapsed += 3
    $status = curl.exe -s "$BaseUrl/documents/$docId" `
        -H "Authorization: Bearer $token" --max-time 10 | ConvertFrom-Json
    Write-Host "  Status: $($status.status) (chunks: $($status.chunkCount))" -ForegroundColor Gray
} while ($status.status -eq "PROCESSING" -and $elapsed -lt $maxWait)

if ($status.status -eq "COMPLETED") {
    Write-Host "  Documento indexado com $($status.chunkCount) chunks!" -ForegroundColor Green
} elseif ($status.status -eq "FAILED") {
    Write-Host "  ERRO no processamento: $($status.errorMessage)" -ForegroundColor Red
    Remove-Item $tmpFile -Force -ErrorAction SilentlyContinue
    exit 1
} else {
    Write-Host "  Timeout aguardando processamento." -ForegroundColor DarkYellow
}

Remove-Item $tmpFile -Force -ErrorAction SilentlyContinue

# --- 6. PERGUNTAR AO RAG ---
Write-Host "[6/6] Perguntar ao RAG..." -ForegroundColor Yellow
$json = '{"title":"Teste RAG"}'
$raw = Post-Json -Url "$BaseUrl/chat/sessions" -Body $json -AuthToken $token
$sessao = $raw | ConvertFrom-Json
if (-not $sessao.id) {
    Write-Host "  ERRO ao criar sessao: $raw" -ForegroundColor Red
    exit 1
}
Write-Host "  Sessao criada: $($sessao.id)" -ForegroundColor Green

$pergunta = "O que e Clean Architecture e quais sao seus principios?"
Write-Host "  Pergunta: $pergunta" -ForegroundColor White
Write-Host "  Aguardando resposta (OpenRouter pode levar ate 30s)..." -ForegroundColor DarkYellow

$json = "{`"chatSessionId`":`"$($sessao.id)`",`"content`":`"$pergunta`"}"
$raw = Post-Json -Url "$BaseUrl/chat/messages" -Body $json -AuthToken $token -Timeout 120

$resposta = $raw | ConvertFrom-Json
if (-not $resposta.content) {
    Write-Host "  ERRO na pergunta: $raw" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  RESPOSTA DA IA" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "$($resposta.content)" -ForegroundColor White

if ($resposta.sources.Count -gt 0) {
    Write-Host ""
    Write-Host "--- FONTES UTILIZADAS ($($resposta.sources.Count)) ---" -ForegroundColor Cyan
    foreach ($src in $resposta.sources) {
        Write-Host "  Documento: $($src.documentName)" -ForegroundColor Gray
        Write-Host "  Trecho #$($src.chunkIndex) (score: $($src.score))" -ForegroundColor Gray
        Write-Host "  Excerto: $($src.excerpt.Substring(0, [Math]::Min(100, $src.excerpt.Length)))..." -ForegroundColor DarkGray
        Write-Host ""
    }
} else {
    Write-Host ""
    Write-Host "Nenhuma fonte encontrada nos documentos." -ForegroundColor DarkYellow
}
