const fs = require("fs");
const path = require("path");

// Directory to scan
const baseDir = "./src/app/shared/components"; // Adjust to your base directory
const outputFile = "./src/assets/i18n/structure.json"; // Output JSON file

// Function to generate keys from filenames
function generateTranslationKeys(dir) {
  const keys = {};
  const files = fs.readdirSync(dir);

  files.forEach((file) => {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      // Recurse into subdirectories
      keys[file.toUpperCase()] = generateTranslationKeys(fullPath);
    } else if (
      file.endsWith(".component.html") ||
      file.endsWith(".component.ts")
    ) {
      // Generate key for each component
      const name = file
        .replace(/\.component\.(html|ts)/, "")
        .toUpperCase()
        .replace(/-/g, "_");
      keys[name] = "PLACEHOLDER_TEXT";
    }
  });

  return keys;
}

// Generate the translation structure
const translationStructure = generateTranslationKeys(baseDir);

// Save to file
fs.writeFileSync(
  outputFile,
  JSON.stringify(translationStructure, null, 2),
  "utf-8"
);

console.log(`Translation structure generated in: ${outputFile}`);
