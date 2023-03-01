export type OpenApiMethod =
  "get"
  | "post"
  | "delete"
  | "put"
  | "options"
  | "head"
  | "patch";
export type OpenApiParameterSchema = {
  type?: string;
  format?: string;
  $ref?: string;
};

export type OpenApiComponentSchema = {
  type?: string;
  format?: string;
};
export type OpenApiParameter = {
  in?: string;
  name: string;
  example?: string;
  required?: boolean;
  schema: OpenApiComponentSchema;
};

export type OpenApiInfo = {
  title: string;
  description: string;
  termsOfService: string;
  license: {
    name: string;
    url: string;
  };
  version: string;
};

export type OpenApiRequestInfo = {
  operationId: string;
  summary: string;
  description: string;
  tags: string[];
  parameters: OpenApiParameter[];
  requestBody: {
    content: {
      [k: string]: {
        schema: OpenApiParameterSchema;
      };
    };
    required: boolean;
  };
  response: unknown;
};

export interface OpenApiAllDetails {
  openapi: string;
  info: OpenApiInfo;
  paths: {
    [k in string]: {
      [k in string]: OpenApiRequestInfo;
    };
  };
  components: {
    schemas: {
      [k: string]: {
        properties?: {
          [k: string]: OpenApiComponentSchema;
        };
        type: string;
      };
    };
  };
  tags: {
    name: string;
    description: string;
  }[];
}

export interface OpenApiRequest {
  uri: string;
  summary: string;
  tag: string;
  description: string;
  operationId: string;
  method: OpenApiMethod;
  parameters: OpenApiParameter[];
}

export function groupOpenApis(meta: OpenApiAllDetails): {
  [uri: string]: OpenApiRequest[];
} {
  const grouped: { [k: string]: OpenApiRequest[] } = {};

  Object.keys(meta.paths).forEach((uri) => {
    const req = meta.paths[uri];
    Object.entries(req).forEach(([method, info]) => {
      const tag = info.tags[0];
      const removed = {
        uri: uri,
        summary: info.summary,
        tag,
        description: info.description,
        operationId: info.operationId,
        method: method as OpenApiMethod,
        parameters: info.parameters,
      };
      const savedApi = grouped[tag];
      if (savedApi) {
        savedApi.push(removed);
        grouped[tag] = savedApi;
      } else {
        grouped[tag] = [removed];
      }
    });
  });
  return grouped;
}

export type OpenApiResponse = {
  headers: Record<string, string>;
  type?: string;
  code?: number;
  data?: unknown;
};
