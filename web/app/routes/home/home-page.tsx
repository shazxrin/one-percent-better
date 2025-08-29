import { ActionIcon, Center, Group, Image, Stack, Text } from "@mantine/core"
import {
    type ActionFunction,
    type LoaderFunction,
    Form,
    useActionData,
    useLoaderData,
    useNavigation,
} from "react-router"
import React, { useEffect } from "react"
import { IconRefresh } from "@tabler/icons-react"
import apiClient from "~/api/api-client"
import z from "zod/v4"
import { notifications } from "@mantine/notifications"
import { LocalDate, LocalDateTime } from "@js-joda/core";

type LoaderData = {
    lastUpdatedDate: string
    noOfCheckIns: number
    streak: number
}

export const loader: LoaderFunction = async ({}): Promise<LoaderData> => {
    const now = LocalDateTime.now()
    const getCheckInsTodayResponse = await apiClient.GET(
        "/api/check-ins/projects/daily-summaries/aggregate/{date}",
        {
            params: {
                path: {
                    date: now.toLocalDate().toString()
                }
            }
        }
    )

    if (getCheckInsTodayResponse.error) {
        throw Response.error()
    }

    return {
        lastUpdatedDate: now.toString(),
        noOfCheckIns: getCheckInsTodayResponse.data.noOfCheckIns ?? 0,
        streak: getCheckInsTodayResponse.data.streak ?? 0
    }
}

const actionFormDataSchema = z.object({
    intent: z.enum(["check-in"])
})

type ActionData = {
    intent: "check-in"
    success: boolean
}

export const action: ActionFunction = async ({ request }): Promise<ActionData> => {
    const formData = await request.formData()
    const parsedFormData = actionFormDataSchema.safeParse(Object.fromEntries(formData))
    if (!parsedFormData.success) {
        console.error("Invalid form data", parsedFormData.error)
        throw new Response("Invalid form data", { status: 400 })
    }
    const { intent } = parsedFormData.data
    if (intent === "check-in") {
        const today = LocalDate.now()

        const checkInTodayResponse = await apiClient.POST(
            "/api/check-ins/projects/all",
            {
                params: {
                    query: {
                        date: today.toString()
                    }
                }
            }
        )

        if (checkInTodayResponse.error) {
            console.error("Failed to check-in today", checkInTodayResponse.error)

            return {
                intent: "check-in",
                success: false
            }
        }

        return {
            intent: "check-in",
            success: true
        }
    }

    throw new Response("Invalid intent", { status: 400 })
}

const Home: React.FC = () => {
    const { lastUpdatedDate, noOfCheckIns, streak } = useLoaderData<LoaderData>()
    const actionData = useActionData<ActionData>()
    const navigation = useNavigation()

    useEffect(() => {
        if (!actionData) {
            return
        }

        if (actionData.intent === "check-in") {
            if (actionData.success) {
                notifications.show({
                    icon: <IconRefresh size={16} />,
                    title: "Refresh check-in successful",
                    message: "Check in refreshed successfully.",
                    color: "blue"
                })
            } else {
                notifications.show({
                    icon: <IconRefresh size={16} />,
                    title: "Failed to refresh check-in",
                    message: "Check in cannot be refreshed.",
                    color: "red"
                })
            }
        }
    }, [actionData])

    return (
        <Stack h={"100%"}>
            <Group gap={"xs"} align={"center"} justify={"flex-end"}>
                <Text size={"xs"} c={"dimmed"} fs={"italic"}>Last updated: {lastUpdatedDate}</Text>
                <Form method={"post"} action={"/?index"}>
                    <input type="hidden" name="intent" value="check-in" />
                    <ActionIcon color={"green"} variant={"subtle"} type={"submit"} loading={navigation.state === "submitting"}>
                        <IconRefresh size={16} />
                    </ActionIcon>
                </Form>
            </Group>

            <Center h={"100%"}>
                <Stack align={"center"} justify="center" gap={"xl"}>
                    <Stack align={"center"} gap={"xs"}>
                        <Image src={ noOfCheckIns > 0 ? "/assets/streak.png" : "/assets/streak_empty.png"} h={"128px"} w={"128px"} />
                        <Text fw={"bold"} size={"4rem"} c={ noOfCheckIns > 0 ? "orange.6" : "dark.1"}>{streak}</Text>
                        <Text fw={"bold"} size={"2rem"} c={ noOfCheckIns > 0 ? "orange.6" : "dark.1"}>days</Text>
                    </Stack>

                    <Text c={ noOfCheckIns > 0 ? "orange.4" : "dark.2"}>You have commited {noOfCheckIns} times today</Text>
                </Stack>
            </Center>
        </Stack>
    )
}
export default Home
