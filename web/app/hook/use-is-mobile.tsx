import { em } from "@mantine/core"
import { useMediaQuery } from "@mantine/hooks"

export default function useIsMobile() {
  return useMediaQuery(`(max-width: ${em(750)})`)
}
