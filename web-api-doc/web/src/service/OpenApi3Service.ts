import {api} from "boot/axios";
import {
  groupOpenApis,
  OpenApiAllDetails,
  OpenApiInfo,
  OpenApiRequest,
} from "src/service/entity/OpenApiAllDetails";

export const OpenApiDetailsService = {
  paths: (details: OpenApiAllDetails): { [uri: string]: OpenApiRequest[] } => {
    return groupOpenApis(details);
  },
  all: async (): Promise<OpenApiAllDetails> => {
    return await api.get<OpenApiAllDetails>("/v3/api-docs").then((d) => d.data);
  },
  info: (details: OpenApiAllDetails): OpenApiInfo => {
    return details.info;
  },
};
