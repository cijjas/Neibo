// src/app/shared/utils/url-safe-base64.util.ts

export function encodeUrlSafeBase64(str: string): string {
  const base64 = btoa(str);
  // Replace '+' with '-', '/' with '_' and remove any trailing '=' characters.
  return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

export function decodeUrlSafeBase64(str: string): string {
  // Calculate required padding.
  const padLength = 4 - (str.length % 4);
  const padded = padLength < 4 ? str + '='.repeat(padLength) : str;
  const base64 = padded.replace(/-/g, '+').replace(/_/g, '/');
  return atob(base64);
}
