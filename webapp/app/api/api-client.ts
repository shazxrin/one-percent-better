import createClient from "openapi-fetch";
import type { paths } from "~/api/schema";

const apiClient = createClient<paths>({
    baseUrl: window.location.origin
})

export default apiClient;
