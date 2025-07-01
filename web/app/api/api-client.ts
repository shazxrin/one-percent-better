import createClient from "openapi-fetch";
import type { paths } from "~/api/schema";

const apiClient = createClient<paths>({
    baseUrl: process.env.APP_URL
});
export default apiClient;
