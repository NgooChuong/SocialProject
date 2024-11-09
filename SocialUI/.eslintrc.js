module.exports = {
  parser: '@typescript-eslint/parser',  // Sử dụng parser cho TypeScript
  parserOptions: {
    ecmaVersion: 2020,  // Cho phép cú pháp ECMAScript mới
    sourceType: 'module',  // Cho phép sử dụng import/export
  },
  extends: [
    'eslint:recommended',  // Các quy tắc cơ bản của ESLint
    'plugin:@typescript-eslint/recommended',  // Các quy tắc cơ bản của TypeScript
  ],
  env: {
    browser: true,  // Cho phép môi trường browser
    node: true,  // Cho phép môi trường Node.js nếu cần
    es6: true,  // Cho phép cú pháp ES6
  },
  plugins: [
    'react',  // Plugin React nếu bạn đang dùng React
    '@typescript-eslint',  // Plugin cho TypeScript
  ],
  rules: {
    'react/react-in-jsx-scope': 'off',  // Tắt lỗi nếu không cần import React (React 17+)
    'react/jsx-uses-react': 'off',  // Tắt lỗi nếu không sử dụng import React (React 17+)
    '@typescript-eslint/no-explicit-any': 'warn',  // Cảnh báo khi sử dụng `any`
    '@typescript-eslint/explicit-module-boundary-types': 'warn',  // Cảnh báo khi không chỉ định kiểu trả về trong function
  },
};
