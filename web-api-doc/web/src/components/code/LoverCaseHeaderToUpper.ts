export function wrapperHeader(headerName: string): string {
  let result = "";
  headerName
    .trim()
    .split("-")
    .map((d) => d.slice(0, 1).toUpperCase() + d.slice(1).toLowerCase())
    .forEach((d) => {
      result += d + "-";
    });
  return result.trim().slice(0, result.lastIndexOf("-"));
}
