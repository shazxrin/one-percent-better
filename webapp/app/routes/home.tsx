import type { Route } from "./+types/home";
import { Center, Title } from "@mantine/core";
import { type ClientLoaderFunction, type ClientLoaderFunctionArgs, useLoaderData } from "react-router";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "One Percent Better" },
        { name: "description", content: "One Percent Better" },
    ];
}

type LoaderData = {
    value: string
}

const clientLoader: ClientLoaderFunction = ({}: ClientLoaderFunctionArgs): LoaderData => {
    return {
        value: ""
    }
}

export default function Home() {
    const { value } = useLoaderData<LoaderData>()

    return (
        <Center w={ "100dvw" } h={ "100dvh" }>
            <Title order={ 1 }>One Percent Better</Title>
        </Center>
    );
}
