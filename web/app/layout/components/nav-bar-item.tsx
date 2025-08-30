import { NavLink } from "@mantine/core"
import { IconHome } from "@tabler/icons-react"
import { Link, useLocation } from "react-router";

type NavBarItemProps = {
    text: string;
    path: string;
    icon: React.ReactNode
    onClick?: () => void;
}

const NavBarItem: React.FC<NavBarItemProps> = ({ text, path, icon, onClick }) => {
    const location = useLocation();

    return (
        <NavLink
            color={ "gray" }
            label={ text }
            leftSection={ icon }
            active={ path === location.pathname }
            component={ Link }
            to={ path }
            onClick={ onClick }
        >
        </NavLink>
    )
}
export default NavBarItem;