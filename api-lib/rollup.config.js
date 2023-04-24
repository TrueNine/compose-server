import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';
// @ts-ignore
import typescript from 'rollup-plugin-typescript';
import pkg from "./package.json" assert {type: "json"}
import {defineConfig} from "rollup";

export default defineConfig({
  input: "index.ts",
  output: {
    file: pkg.main,
    format: "umd"
  },
  plugins: [
    resolve(),
    commonjs(),
    typescript()
  ]
})
