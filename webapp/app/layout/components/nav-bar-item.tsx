import { Button } from "@mantine/core"
import { IconHome } from "@tabler/icons-react"
import { Link, useLocation } from "react-router";

type NavBarItemProps = {
    text: string;
    path: string;
    icon: React.ReactNode
}

const NavBarItem: React.FC<NavBarItemProps> = ({ text, path, icon }) => {
    const location = useLocation();

    return (
        <Button 
            color={"gray"}
            leftSection={ icon } 
            justify={ "flex-start" }
            variant={ path === location.pathname ? "light" : "subtle" }
            component={ Link }
            to={ path }
        >
            { text }
        </Button>
    )
}
export default NavBarItem;