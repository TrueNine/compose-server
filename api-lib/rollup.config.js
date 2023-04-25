import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';
import typescript from 'rollup-plugin-typescript';
import {defineConfig} from "rollup";
import dts from "rollup-plugin-dts";
import copy from "rollup-plugin-copy";
import del from "rollup-plugin-delete";
import tserver from "@rollup/plugin-terser";

export default defineConfig([
  {
    preserveModules: true,
    input: "src/index.ts",
    output: {
      dir: "es",
      format: "esm"
    },
    plugins: [
      del({
        targets: ["es/*"]
      }),
      resolve(),
      commonjs(),
      typescript(),
      copy(
        {
          targets: [
            {
              src: "package.json",
              dest: "es"
            }
          ]
        }
      ),
      tserver({
        ecma: 2020,
        ie8: false
      })
    ]
  }, {
    preserveModules: true,
    input: "src/index.ts",
    plugins: [dts()],
    output: {
      format: "esm",
      dir: "es"
    }
  }
])
