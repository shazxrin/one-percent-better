import type { Route } from "./+types/home";
import { AppShell, Burger, Button, Center, Group, Skeleton, Title } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "One Percent Better" },
        { name: "description", content: "One Percent Better" },
    ];
}

export default function Home() {
    return (
        <Center w={ "100dvw" } h={ "100dvh" }>
            <Title order={ 1 }>One Percent Better</Title>
        </Center>
    );
}
