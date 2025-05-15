import type { Route } from "./+types/home";
import { Center, Group, Stack, Text, Title, useMantineTheme } from "@mantine/core";
import { type ClientLoaderFunction, type ClientLoaderFunctionArgs, useLoaderData } from "react-router";
import React from "react";
import { IconFlame } from "@tabler/icons-react";
import apiClient from "~/api/api-client"

export const meta = ({}: Route.MetaArgs) => {
    return [
        { title: "One Percent Better" },
        { name: "description", content: "One Percent Better" },
    ];
};

type LoaderData = {
    count: number;
    streak: number;
};

export const clientLoader: ClientLoaderFunction = async ({}: ClientLoaderFunctionArgs): Promise<LoaderData> => {
    const getCheckInsTodayResponse = await apiClient.GET("/api/check-ins/today");

    if (getCheckInsTodayResponse.error) {
        throw Response.error();
    }

    return {
        count: getCheckInsTodayResponse.data.count!!,
        streak: getCheckInsTodayResponse.data.streak!!
    };
};

const page: React.FC = () => {
    const { count, streak } = useLoaderData<LoaderData>();
    const theme = useMantineTheme()

    return (
        <Center w={ "100dvw" } h={ "100dvh" }>
            <Stack align={ "center" } justify="center" gap={ "lg" }>
                <Group align="center">
                    <IconFlame size={ "8rem" } color={ count > 0 ? theme.colors.orange[6] : theme.colors.dark[1] } />
                    <Text fw={ "bold" } size={ "12rem" } c={ count > 0 ? "orange.6" : "dark.1" }>{ streak }</Text>
                </Group>

                <Text c={ count > 0 ? "orange.4" : "dark.2" }>You have commited { count } times today</Text>
            </Stack>
        </Center>
    );
};
export default page;
