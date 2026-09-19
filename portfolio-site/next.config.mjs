/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // Static export for drag-drop hosting (Netlify Drop / Cloudflare Pages) without Vercel login
  output: "export",
  images: { unoptimized: true },
};

export default nextConfig;
