import hj from "highlight.js/lib/common";
import beautify from "js-beautify";

import json from "highlight.js/lib/languages/json";
import java from "highlight.js/lib/languages/java";
import xml from "highlight.js/lib/languages/xml";

hj.configure({
  throwUnescapedHTML: false,
});
const languages = [java, xml, java, json];
languages.forEach((l) => hj.registerLanguage(l.name, l));

export type Lang = "json" | "javascript" | "xml" | "css";

export const formatCodeToHtml = (code: unknown, lang: Lang): string => {
  if (lang != "json" && typeof code === "string") {
    switch (lang) {
      case "javascript":
        code = beautify.js(code);
        break;
      case "xml":
        code = beautify.html(code);
        break;
      case "css":
        code = beautify.css(code);
        break;
    }
  } else {
    code = JSON.stringify(code, null, 2);
  }
  code = `\n${code}`;
  return hj.highlightAuto(code as string, [lang]).value;
};
