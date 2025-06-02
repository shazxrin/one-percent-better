import { ActionIcon, Anchor, Button, Divider, Group, Modal, Stack, Table, TextInput, Title } from "@mantine/core"
import { IconExclamationCircle, IconPlus, IconTrash } from "@tabler/icons-react"
import { z } from "zod/v4"
import { Form, useLoaderData, type ClientActionFunction, type ClientLoaderFunction } from "react-router"
import apiClient from "~/api/api-client"
import { notifications } from "@mantine/notifications"
import { useDisclosure } from "@mantine/hooks"

type LoaderData = {
    projects: {
        owner?: string | undefined,
        name?: string | undefined
    }[]
}

export const clientLoader: ClientLoaderFunction = async (): Promise<LoaderData> => {
    const getProjectsResponse = await apiClient.GET("/api/projects")
    if (getProjectsResponse.error) {
        throw Response.error()
    }

    const projects = getProjectsResponse.data

    return {
        projects: projects
    }
}

const actionFormDataSchema = z.object({
    intent: z.enum(["delete", "add"]),
    owner: z.string().min(1, "Owner is required"),
    name: z.string().min(1, "Name is required"),
})
export const clientAction: ClientActionFunction = async ({ request }): Promise<void> => {
    const formData = await request.formData()
    const parsedFormData = actionFormDataSchema.safeParse(Object.fromEntries(formData))
    if (!parsedFormData.success) {
        console.error("Invalid form data", parsedFormData.error)
        throw new Response("Invalid form data", { status: 400 })
    }
    const { intent, owner, name } = parsedFormData.data
    if (intent === "delete") {
        const deleteProjectResponse = await apiClient.DELETE(`/api/projects`, {
            body: {
                owner: owner,
                name: name,
            }
        })

        if (deleteProjectResponse.error) {
            notifications.show({
                icon: <IconExclamationCircle size={16} />,
                title: "Failed to delete project",
                message: "An error occurred while deleting the project.",
                color: "red"
            })
            return
        }

        notifications.show({
            icon: <IconTrash size={16} />,
            title: "Project deleted",
            message: `${owner}/${name} has been deleted successfully.`,
            color: "red"
        })
    } else if (intent === "add") {
        const addProjectResponse = await apiClient.POST("/api/projects", {
            body: {
                owner: owner,
                name: name,
            }
        })

        if (addProjectResponse.error) {
            notifications.show({
                icon: <IconExclamationCircle size={16} />,
                title: "Failed to add project",
                message: "An error occurred while adding the project.",
                color: "red"
            })

            return
        }

        notifications.show({
            icon: <IconPlus size={16} />,
            title: "Project added",
            message: `${owner}/${name} has been added successfully.`,
            color: "green"
        })
    }

    return
}

const ProjectsPage: React.FC = () => {
    const { projects } = useLoaderData<LoaderData>()

    const [addProjectModalOpened, { open: openAddProjectModal, close: closeAddProjectModal }] = useDisclosure(false)

    return (
        <Stack>
            <Title order={2}>Projects</Title>

            <Divider my={"sm"} />

            <Group>
                <Button 
                    leftSection={<IconPlus size={16} />}
                    variant={"light"}
                    color={"green"}
                    onClick={openAddProjectModal}
                >
                    Add
                </Button>
            </Group>

            <Modal opened={addProjectModalOpened} onClose={closeAddProjectModal} title={"Add Project"}>
                <Form method={"post"} action={"/projects"} onSubmit={closeAddProjectModal}>
                    <input type="hidden" name="intent" value="add" />

                    <Stack w={"100%"}>
                        <TextInput name={"owner"} label={"Owner"} description={"Owner of the project on GitHub."} placeholder={"Enter owner name"} />
                        <TextInput name={"name"} label={"Name"} description={"Name of the project on GitHub."} placeholder={"Enter project name"} />
                        
                        <Button type={"submit"} fullWidth>Add</Button>
                    </Stack>
                </Form>
            </Modal>

            <Table.ScrollContainer minWidth={"500"}>
                <Table>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>Project</Table.Th>
                            <Table.Th>Actions</Table.Th>
                        </Table.Tr>

                        {projects.map((project) => {
                            const fullName = project.owner + "/" + project.name

                            return (
                                <Table.Tr key={fullName}>
                                    <Table.Td>
                                        <Anchor href={`https://github.com/${fullName}`}>{fullName}</Anchor>
                                    </Table.Td>
                                    <Table.Td>
                                        <Group>
                                            <Form method={"delete"}>
                                                <input type="hidden" name="intent" value="delete" />
                                                <input type="hidden" name="owner" value={project.owner} />
                                                <input type="hidden" name="name" value={project.name} />
                                                <ActionIcon color={"red"} variant={"light"} type={"submit"}>
                                                    <IconTrash size={16} />
                                                </ActionIcon>
                                            </Form>
                                        </Group>
                                    </Table.Td>
                                </Table.Tr>
                            )
                        })}
                    </Table.Thead>
                </Table>
            </Table.ScrollContainer>
        </Stack>
    )
}
export default ProjectsPage
