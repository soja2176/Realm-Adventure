$file = "app\src\main\java\com\example\ui\GameScreens.kt"
$content = Get-Content $file -Raw

# Only replace maxLines that don't already have overflow
# Pattern 1: maxLines = N, (followed by newline, no overflow on next lines)
$content = $content -replace '(maxLines\s*=\s*\d+),(?!\s*overflow[^)]*\))(\s*//.*)?\r?\n(?![^)]*overflow)', '$1, overflow = TextOverflow.Ellipsis,'

# Pattern 2: maxLines = N at end of Text(params) - no overflow already present
$content = $content -replace '(maxLines\s*=\s*\d+)(?!\s*[^)]*overflow)\s*\)', '$1, overflow = TextOverflow.Ellipsis)'

Set-Content $file -Value $content -Encoding UTF8
Write-Host "Done fixing overflow in GameScreens.kt"

# Also fix NewScreens.kt  
$file2 = "app\src\main\java\com\example\ui\NewScreens.kt"
$content2 = Get-Content $file2 -Raw
$content2 = $content2 -replace '(maxLines\s*=\s*\d+)(?!\s*[^)]*overflow)\s*\)', '$1, overflow = TextOverflow.Ellipsis)'
Set-Content $file2 -Value $content2 -Encoding UTF8
Write-Host "Done fixing overflow in NewScreens.kt"