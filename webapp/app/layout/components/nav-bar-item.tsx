import { NavLink } from "@mantine/core"
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
        <NavLink
            color={"gray"}
            label={ text }
            leftSection={ icon } 
            active={ path === location.pathname }
            component={ Link }
            to={ path }
        >
        </NavLink>
    )
}
export default NavBarItem;