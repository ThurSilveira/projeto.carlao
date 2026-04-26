/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Primary — warm brown (replaces blue)
        primary: {
          50:  '#fff8f0',
          100: '#FFDDAE',
          200: '#f0c07a',
          300: '#e6a550',
          400: '#c07830',
          500: '#875025',
          600: '#583400',
          700: '#432700',
          800: '#2e1a00',
          900: '#1C0A00',
          950: '#0f0800',
        },
        // Secondary — teal (replaces purple)
        secondary: {
          50:  '#f0fdfb',
          100: '#CCFBF1',
          200: '#99f6e4',
          300: '#5eead4',
          400: '#2dd4bf',
          500: '#14B8A6',
          600: '#0F766E',
          700: '#0d6460',
          800: '#134E4A',
          900: '#042f2e',
        },
        // Tertiary — amber
        amber: {
          50:  '#fffbeb',
          100: '#FEF3C7',
          200: '#fde68a',
          300: '#fcd34d',
          400: '#fbbf24',
          500: '#f59e0b',
          600: '#D97706',
          700: '#B45309',
          800: '#92400e',
          900: '#78350f',
        },
        // Success — green
        success: {
          50:  '#f0fdf4',
          100: '#DCFCE7',
          600: '#16A34A',
          700: '#15803d',
          800: '#4D7C0F',
          900: '#14532d',
        },
        // Info — blue
        info: {
          50:  '#eff6ff',
          100: '#DBEAFE',
          600: '#2563EB',
          700: '#1d4ed8',
        },
        // Purple — for KPI escalas aprovadas
        purple: {
          100: '#EDE9FE',
          600: '#7C3AED',
          700: '#6d28d9',
        },
        // Neutral surface tokens
        neutral: {
          100: '#F1F5F9',
          200: '#E2E8F0',
          300: '#CBD5E1',
          400: '#94A3B8',
          500: '#64748B',
          600: '#475569',
          700: '#334155',
          800: '#1E293B',
          850: '#1E2535',
          900: '#141927',
          950: '#0f1421',
        },
      },
      fontFamily: {
        sans:  ['Nunito', 'system-ui', 'sans-serif'],
        serif: ['"Playfair Display"', 'Georgia', 'serif'],
      },
      borderRadius: {
        lg:   '0.875rem',
        xl:   '1rem',
        pill: '9999px',
      },
    },
  },
  plugins: [],
};
