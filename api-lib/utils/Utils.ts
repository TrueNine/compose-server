/**
 * 笛卡尔乘积，用于解包类型数组
 * @param spec {@link Record<string, string>}
 * @param currentIndex 当前索引 默认 0
 * @param currentCombination 当前穷举组合
 */
export function cartesianProduct(spec: Record<string, string[]>, currentIndex = 0, currentCombination: Record<string, string> = {}): Record<string, string>[] {
  if (currentIndex === Object.keys(spec).length) {
    return [currentCombination];
  }
  const currentKey = Object.keys(spec)[currentIndex];
  const currentValues = spec[currentKey];

  const result: Record<string, string>[] = [];
  for (let i = 0; i < currentValues.length; i++) {
    const nextCombination = { ...currentCombination };
    nextCombination[currentKey] = currentValues[i];
    result.push(...cartesianProduct(spec, currentIndex + 1, nextCombination));
  }
  return result;
}

/**
 * 取数组交集
 * @param a1
 * @param a2
 */
export function arrayDiff<T>(a1: T[], a2: T[]): T[] {
  const set1 = new Set(a1);
  const set2 = new Set(a2);
  const first = set1.size >= set2.size ? set1 : set2;
  const last = set1.size < set2.size ? set1 : set2;
  return Array.from(first).filter((i) => !last.has(i));
}
