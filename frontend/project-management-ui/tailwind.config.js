/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#06b6d4'
        },
        dark: '#0f172a'
      }
    }
  },
  plugins: []
}
