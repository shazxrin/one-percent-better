import { type RouteConfig, index, route } from "@react-router/dev/routes"

const routes: RouteConfig = [
    index("routes/home/home-page.tsx"),
    route("projects", "routes/projects/projects-page.tsx"),
]
export default routes
