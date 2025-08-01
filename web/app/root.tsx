import "@mantine/core/styles.css"
import '@mantine/notifications/styles.css'

import { isRouteErrorResponse, Links, Meta, Scripts, ScrollRestoration } from "react-router"
import type { Route } from "./+types/root"
import { ColorSchemeScript, mantineHtmlProps, MantineProvider } from "@mantine/core"
import { Notifications } from "@mantine/notifications"
import React from "react"
import AppLayout from "~/layout/app-layout"

export const Layout = ({ children }: { children: React.ReactNode }) => (
    <html lang="en" {...mantineHtmlProps}>
        <head>
            <meta charSet="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <ColorSchemeScript defaultColorScheme={"auto"} />
            <Meta />
            <Links />
        </head>
        <body>
            <MantineProvider defaultColorScheme={"auto"}>
                <Notifications position={"bottom-center"} />
                {children}
            </MantineProvider>
            <ScrollRestoration />
            <Scripts />
        </body>
    </html>
)

const App: React.FC = () => <AppLayout />
export default App

export const ErrorBoundary = ({ error }: Route.ErrorBoundaryProps) => {
    let message = "Oops!"
    let details = "An unexpected error occurred."
    let stack: string | undefined

    if (isRouteErrorResponse(error)) {
        message = error.status === 404 ? "404" : "Error"
        details =
            error.status === 404
                ? "The requested page could not be found."
                : error.statusText || details
    } else if (import.meta.env.DEV && error && error instanceof Error) {
        details = error.message
        stack = error.stack
    }

    return (
        <main className="pt-16 p-4 container mx-auto">
            <h1>{message}</h1>
            <p>{details}</p>
            {
                stack &&
                (
                    <pre className="w-full p-4 overflow-x-auto">
                        <code>{stack}</code>
                    </pre>
                )
            }
        </main>
    )
}
