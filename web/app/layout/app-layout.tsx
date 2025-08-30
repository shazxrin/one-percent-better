import { AppShell, Group, Burger, Text, Stack, ActionIcon, } from "@mantine/core"
import { useDisclosure } from "@mantine/hooks"
import { IconBrandGithub, IconHome, IconLayoutSidebar } from "@tabler/icons-react"
import { Outlet } from "react-router"
import useIsMobile from "~/hook/use-is-mobile"
import NavBarItem from "~/layout/components/nav-bar-item"

const AppLayout = () => {
    const [isMobileOpened, { toggle: mobileToggle}] = useDisclosure()
    const [isDesktopOpened, { toggle: desktopToggle}] = useDisclosure(true)
    const isMobile = useIsMobile()

    const toggle = () => {
        if (isMobile) {
            mobileToggle()
        } else {
            desktopToggle()
        }
    }

    const onClickNavBarItem = () => {
        if (isMobile) {
            mobileToggle()
        }
    }

    return (
        <AppShell
            header={ { height: 48 } }
            navbar={ { width: 300, breakpoint: "sm", collapsed: { mobile: !isMobileOpened, desktop: !isDesktopOpened } } }
            padding={ "md" }
            h={ "100dvh" }
        >
            <AppShell.Header>
                <Group h={ "100%" } px={ 8 } gap={ "sm" }>
                    <ActionIcon onClick={ toggle } variant={ "subtle" } color={ "gray" } size={ "md" }>
                        <IconLayoutSidebar size={ 18 } />
                    </ActionIcon>

                    <Text fw={ "bold" } size={ "md" }>OPB</Text>
                </Group>
            </AppShell.Header>

            <AppShell.Navbar>
                <Stack gap={ "0" }>
                    <NavBarItem text={ "Home" } path={ "/" } icon={ <IconHome size={ 18 }/> } onClick={ onClickNavBarItem }/>
                    <NavBarItem text={ "Projects" } path={ "/projects" } icon={ <IconBrandGithub size={ 18 }/> } onClick={ onClickNavBarItem }/>
                </Stack>
            </AppShell.Navbar>

            <AppShell.Main h={ "100%" }>
                <Outlet/>
            </AppShell.Main>
        </AppShell>
    )
}
export default AppLayout