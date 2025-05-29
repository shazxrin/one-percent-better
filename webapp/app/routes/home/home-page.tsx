import { Center, Image, Stack, Text, useMantineTheme } from "@mantine/core"
import { type ClientLoaderFunction, type ClientLoaderFunctionArgs, useLoaderData } from "react-router"
import React from "react"
import { IconFlame } from "@tabler/icons-react"
import apiClient from "~/api/api-client"

type LoaderData = {
    count: number
    streak: number
}

export const clientLoader: ClientLoaderFunction = async ({ }: ClientLoaderFunctionArgs): Promise<LoaderData> => {
    const getCheckInsTodayResponse = await apiClient.GET("/api/check-ins/today")

    if (getCheckInsTodayResponse.error) {
        throw Response.error()
    }

    return {
        count: getCheckInsTodayResponse.data.count!!,
        streak: getCheckInsTodayResponse.data.streak!!
    }
}

const Home: React.FC = () => {
    const { count, streak } = useLoaderData<LoaderData>()

    return (
        <Center h={"100%"}>
            <Stack align={"center"} justify="center" gap={"xl"}>
                <Stack align={"center"} gap={"xs"}>
                    <Image src={count > 0 ? "/assets/streak.png" : "/assets/streak_empty.png"} h={"128px"} w={"128px"} />
                    <Text fw={"bold"} size={"4rem"} c={count > 0 ? "orange.6" : "dark.1"}>{streak}</Text>
                    <Text fw={"bold"} size={"2rem"} c={count > 0 ? "orange.6" : "dark.1"}>days</Text>
                </Stack>

                <Text c={count > 0 ? "orange.4" : "dark.2"}>You have commited {count} times today</Text>
            </Stack>
        </Center>
    )
}
export default Home
