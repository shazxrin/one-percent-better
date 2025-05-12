import type { Route } from "./+types/home";
import { Center, Title } from "@mantine/core";
import { type ClientLoaderFunction, type ClientLoaderFunctionArgs, useLoaderData } from "react-router";
import React from "react";
import apiClient from "~/api/api-client"

export const meta = ({}: Route.MetaArgs) => {
    return [
        { title: "One Percent Better" },
        { name: "description", content: "One Percent Better" },
    ];
};

type LoaderData = {
    value: string
};

export const clientLoader: ClientLoaderFunction = async ({}: ClientLoaderFunctionArgs): Promise<LoaderData> => {
    return {
        value: ""
    };
};

const page: React.FC = () => {
    const { value } = useLoaderData<LoaderData>();

    return (
        <Center w={ "100dvw" } h={ "100dvh" }>
            <Title order={ 1 }>One Percent Better</Title>
        </Center>
    );
};
export default page;
