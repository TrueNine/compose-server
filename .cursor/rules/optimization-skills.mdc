---
description: Coding Optimization Guidelines
globs: 
alwaysApply: false
---
## Prefer Early Returns

Use early returns to reduce nesting and improve code readability.

```ts
 // Bad: Deeply nested conditions
function processOrder(order) {
  if (order) {
    if (order.items) {
      if (order.items.length > 0) {
        if (order.status === 'pending') {
          // Process order
        }
      }
    }
  }
}

// Good: Flat structure with early returns
function processOrder(order) {
  if (!order) return;
  if (!order.items?.length) return;
  if (order.status !== 'pending') return;
  // Process order
}
```

## Use Lazy Initialization

Lazy initialization delays computation or resource allocation until actually needed, enhancing performance. Common use cases include:

**Browser Compatibility Checks:** Detect features (e.g., event listeners) on the first call and cache the appropriate function for subsequent calls, avoiding redundant checks.

```ts
// Example: Event listener function cached after first call
function addEvent(element, type, handler) {
  if (window.addEventListener) {
      addEvent = (element, type, handler) => element.addEventListener(type, handler);
  } else if (window.attachEvent) {
      addEvent = (element, type, handler) => element.attachEvent('on' + type, handler);
  }
  addEvent(element, type, handler); // Call the determined function
}
```

**Resource Loading:** Delay operations like opening files or loading configurations until they are first accessed.

```go
// Example: Configuration loaded only when GetConfig is first called
var config *Config
func GetConfig() *Config {
  if config == nil {
      config = loadConfigFromFile() // Lazy loading
  }
  return config
}
```

## Always Use Braces for Conditionals/Loops

Never omit braces `{}` for `if` or `while` statements, even for single-line bodies, to prevent potential errors and maintain clarity.

```c
// Bad: Missing braces can lead to unexpected behavior (second goto fail executes unconditionally)
if((err = SSLHashSHA1.update(&hashCtx, &signedParams)) != 0)
  // This goto is conditional
  goto fail;
  // This goto is *not* conditional
  goto fail; 
// Good: Braces clearly define the scope
if((err = SSLHashSHA1.update(&hashCtx, &signedParams)) != 0) {
  goto fail;
}
```

## Replace Conditional Logic with Lookups

Transform value-based conditional logic (`if/else if` or `switch`) into table lookups (e.g., using objects or maps) for better readability and often better performance.

```ts
// Good: Using a lookup table
const valueAndLabel = {
  '1': 'label1',
  '2': 'label2',
  '3': 'label3',
};

function getLabel(value) {
  return valueAndLabel[value]; // Direct lookup
}

// Bad: Using if/else if
function getLabelIfElse(value) {
  if (value === '1') {
    return 'label1';
  } else if (value === '2') {
    return 'label2';
  } else if (value === '3') {
    return 'label3';
  }
}

// Bad: Using switch
function getLabelSwitch(value) {
  switch (value) {
    case '1': return 'label1';
    case '2': return 'label2';
    case '3': return 'label3';
  }
}
```

## Avoid Non-Null Assertions

In languages with null safety (like TypeScript, Kotlin, Swift), use non-null assertion operators (`!`) cautiously. Prefer explicit null checks or optional chaining (`?.`) to prevent potential runtime errors.

```ts
// Bad: Risky non-null assertion
function processUserAssertion(user: User | null) {
  console.log(user!.name); // Crashes if user is null
}

// Good: Explicit null check
function processUserCheck(user: User | null) {
  if (!user) {
    return; // Safely handle null case
  }
  console.log(user.name);
}

// Good: Optional chaining
function processUserOptional(user: User | null) {
  console.log(user?.name); // Returns undefined if user is null, no crash
}
```

## Commenting Guidelines

-   **No End-of-Line Comments:** Avoid placing comments at the end of a code line.
-   **Language:** Use English for library code comments. Use Chinese for business logic comments (including documentation comments).
