import { api } from "boot/axios";
import { OpenApiMethod, OpenApiResponse } from "src/service/entity/OpenApiAllDetails";

export async function req(r: {
  method: OpenApiMethod;
  url: string;
  data?: unknown;
  params?: Record<string, unknown>;
  headers?: Record<string, string>;
}): Promise<OpenApiResponse> {
  return new Promise<OpenApiResponse>((resolve, reject) => {
    api
      .request<OpenApiResponse>({
        timeout: 10000,
        method: r.method,
        url: r.url,
        params: r.params,
        headers: r.headers,
        data: r.data,
      })
      .then((d) =>
        resolve({
          data: d.data,
          code: d.status,
          headers: d.headers,
          type: d.headers["content-type"],
        })
      )
      .catch((e) => {
        if (e.response && e.isAxiosError) {
          reject({
            data: e.response.data,
            headers: e.response.headers,
            type: e.response.headers["content-type"],
            code: e.response.status,
          });
        } else {
          console.log({ e });
          reject({
            data: null,
            headers: {},
            type: "application/json",
            code: 404,
          });
        }
      });
  });
}
