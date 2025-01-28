const fs = require("fs");
const path = require("path");
const cheerio = require("cheerio"); // npm install cheerio

// Directory to scan
const baseDir = "./src/app/features"; // Adjust this to your app root
const outputFile = "./src/assets/i18n/en.json"; // Translation file

// Load existing JSON file (if any) or start fresh
let translationKeys = fs.existsSync(outputFile)
  ? JSON.parse(fs.readFileSync(outputFile, "utf-8"))
  : {};

// Recursive function to process directories
function processDirectory(dir) {
  const files = fs.readdirSync(dir);

  files.forEach((file) => {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      processDirectory(fullPath);
    } else if (file.endsWith(".html")) {
      extractKeysFromHtml(fullPath);
    } else if (file.endsWith(".ts")) {
      extractKeysFromTs(fullPath);
    }
  });
}

// Extract static text from HTML files
function extractKeysFromHtml(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const $ = cheerio.load(content);

  const componentName = path
    .basename(filePath)
    .replace(".component.html", "")
    .toUpperCase();

  // Ensure component namespace exists
  if (!translationKeys[componentName]) {
    translationKeys[componentName] = {};
  }

  $("*")
    .contents()
    .filter((_, el) => el.type === "text")
    .each((_, el) => {
      const text = el.nodeValue.trim();
      if (text) {
        const key = generateKey(text);
        if (!translationKeys[componentName][key]) {
          translationKeys[componentName][key] = text;
        }
      }
    });
}

// Extract strings from TypeScript files
function extractKeysFromTs(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const componentName = path
    .basename(filePath)
    .replace(".component.ts", "")
    .toUpperCase();

  // Ensure component namespace exists
  if (!translationKeys[componentName]) {
    translationKeys[componentName] = {};
  }

  // Match strings inside `''` or `""` (simple static strings)
  const stringRegex = /(["'`])((?:(?!\1).)+)\1/g;
  let match;
  while ((match = stringRegex.exec(content)) !== null) {
    const text = match[2].trim();
    if (text.length > 3 && !text.startsWith("{{")) {
      const key = generateKey(text);
      if (!translationKeys[componentName][key]) {
        translationKeys[componentName][key] = text;
      }
    }
  }
}

// Generate a translation key from text
function generateKey(text) {
  return text
    .toUpperCase()
    .replace(/\s+/g, "_")
    .replace(/[^\w]/g, "")
    .slice(0, 50); // Limit key length
}

// Process all files in the base directory
processDirectory(baseDir);

// Write the translation keys to the JSON file
fs.writeFileSync(outputFile, JSON.stringify(translationKeys, null, 2), "utf-8");
console.log(`Translation keys mapped and saved to: ${outputFile}`);
