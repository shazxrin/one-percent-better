import { AppShell, Group, Burger, Text, Stack, } from "@mantine/core"
import { useDisclosure } from "@mantine/hooks"
import { IconBrandGithub, IconHome } from "@tabler/icons-react"
import { Outlet } from "react-router"
import NavBarItem from "~/layout/components/nav-bar-item"

const AppLayout = () => {
    const [opened, { toggle }] = useDisclosure()

    return (
        <AppShell
            header={ { height: 60 } }
            navbar={ { width: 300, breakpoint: "sm", collapsed: { mobile: !opened } } }
            padding={ "md" }
            h={ "100dvh" }
        >
            <AppShell.Header>
                <Group h="100%" px="md">
                    <Burger opened={ opened } onClick={ toggle } hiddenFrom={ "sm" } size={ "sm" }/>
                    <Text fw={ "bold" } size={ "lg" } c={ "dimmed" }>One Percent Better</Text>
                </Group>
            </AppShell.Header>

            <AppShell.Navbar>
                <Stack gap={ "0" }>
                    <NavBarItem text={ "Home" } path={ "/" } icon={ <IconHome size={ 18 }/> }/>
                    <NavBarItem text={ "Projects" } path={ "/projects" } icon={ <IconBrandGithub size={ 18 }/> }/>
                </Stack>
            </AppShell.Navbar>

            <AppShell.Main h={ "100%" }>
                <Outlet/>
            </AppShell.Main>
        </AppShell>
    )
}
export default AppLayout