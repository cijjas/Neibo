const fs = require("fs");
const path = require("path");
const cheerio = require("cheerio"); // npm install cheerio

// Directory to scan
const baseDir = "./src/app/features"; // Adjust as needed
const outputFile = "./src/assets/i18n/structure.json";

let keys = {};

// Recursive function to extract keys from files
function processDirectory(dir) {
  const files = fs.readdirSync(dir);

  files.forEach((file) => {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      processDirectory(fullPath);
    } else if (file.endsWith(".html")) {
      extractKeysFromHtml(fullPath);
    }
  });
}

// Extract static text from HTML
function extractKeysFromHtml(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const $ = cheerio.load(content);
  const componentName = path
    .basename(filePath)
    .replace(".component.html", "")
    .toUpperCase();

  keys[componentName] = {};

  $("*")
    .contents()
    .filter((_, el) => el.type === "text")
    .each((_, el) => {
      const text = el.nodeValue.trim();
      if (text) {
        const key = text.toUpperCase().replace(/\s+/g, "_");
        keys[componentName][key] = "PLACEHOLDER_TEXT";
      }
    });
}

// Generate the keys
processDirectory(baseDir);

// Write the keys to a file
fs.writeFileSync(outputFile, JSON.stringify(keys, null, 2), "utf-8");
console.log(`Translation keys generated at: ${outputFile}`);
