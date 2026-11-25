from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
agent = ROOT / "android/open-jarvis/app/src/main/java/com/openjarvis/agent/AgentCore.kt"
mcp = ROOT / "android/open-jarvis/app/src/main/java/com/openjarvis/mcp/MCPClient.kt"

agent_text = agent.read_text(encoding="utf-8")
marker = "\n    suspend fun testConnection(): Result<Long> {"
if marker not in agent_text:
    raise SystemExit("AgentCore.kt: expected executeTask boundary not found")
before, after = agent_text.split(marker, 1)
if not before.rstrip().endswith("}") or before.rstrip().endswith("}\n    }"):
    pass
# executeTask has scope.launch + withLock closures but is missing its own closing brace.
if not before.endswith("    }\n"):
    raise SystemExit("AgentCore.kt: unexpected ending; refusing unsafe patch")
agent.write_text(before + "    }\n" + marker + after, encoding="utf-8")

mcp_text = mcp.read_text(encoding="utf-8")
old = """                .post(okhttp3.RequestBody.create(
                    "application/json".toByteArray().to okhttp3.MediaType.get("application/json"),
                    requestBody
                ))"""
new = """                .post(requestBody.toRequestBody("application/json".toMediaType()))"""
if old not in mcp_text:
    raise SystemExit("MCPClient.kt: legacy RequestBody.create block not found")
mcp_text = mcp_text.replace(old, new)
imports = "import okhttp3.MediaType.Companion.toMediaType\nimport okhttp3.RequestBody.Companion.toRequestBody\n"
if "import okhttp3.MediaType.Companion.toMediaType" not in mcp_text:
    mcp_text = mcp_text.replace("import okhttp3.Request\n", "import okhttp3.Request\n" + imports)
mcp.write_text(mcp_text, encoding="utf-8")
print("Patched AgentCore.kt and MCPClient.kt successfully.")
