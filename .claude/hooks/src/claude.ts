export interface Data extends Record<string, unknown> {
  session_id: string
  tool_input?: {
    file_path?: string
  }
}

export function readStdin(data: string | null | undefined): Data | undefined {
  if (data === null || data === undefined || data.trim() === '') {
    return void 0
  }
  let jsonData: Data | undefined = void 0
  try {
    jsonData = JSON.parse(data)
  } catch (e) {
    jsonData = void 0
  }
  if (jsonData === void 0) {
    return void 0
  }
  return jsonData
}
