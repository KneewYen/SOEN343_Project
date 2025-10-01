# Frontend Setup Instructions

## Quick Start

1. **Install Node.js** (if not already installed):
   - Download from [nodejs.org](https://nodejs.org/)
   - Required version: 20.19+ or 22.12+

2. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. **Open your browser** and go to `http://localhost:5173`

## What gets installed

When you run `npm install`, the following dependencies will be installed:

- **Vue 3** - Frontend framework
- **Vue Router 4** - Client-side routing
- **Vite** - Build tool and development server
- **Development tools** - ESLint, Prettier, etc.

## Project Structure

```
frontend/
├── src/
│   ├── presentation/         # UI components and pages
│   ├── application/          # Business logic
│   ├── domain/              # Domain entities
│   └── infrastructure/      # Data storage
├── package.json             # Dependencies and scripts
├── vite.config.js           # Vite configuration
└── .gitignore              # Git ignore rules
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

## Troubleshooting

### Node.js Version Issues
If you get a Node.js version warning:
```bash
You are using Node.js 20.12.0. Vite requires Node.js version 20.19+ or 22.12+.
```

**Solution**: Upgrade your Node.js version to 20.19+ or 22.12+

### Port Already in Use
If port 5173 is already in use, Vite will automatically use the next available port (5174, 5175, etc.)

### Dependencies Issues
If you encounter dependency issues:
```bash
rm -rf node_modules package-lock.json
npm install
```

## Git Integration

The `.gitignore` file is configured to exclude:
- `node_modules/` - Dependencies (don't commit these)
- `dist/` - Build output
- `.env` files - Environment variables
- IDE files - VSCode, IntelliJ, etc.
- OS files - .DS_Store, Thumbs.db, etc.

This means you can safely commit your code without including the large `node_modules` folder.
