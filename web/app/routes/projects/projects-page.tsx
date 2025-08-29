import { ActionIcon, Anchor, Button, Divider, Group, Modal, Stack, Table, TextInput, Title } from "@mantine/core"
import { IconExclamationCircle, IconPlus, IconTrash } from "@tabler/icons-react"
import { z } from "zod/v4"
import { type ActionFunction, Form, type LoaderFunction, useActionData, useLoaderData } from "react-router"
import apiClient from "~/api/api-client"
import { notifications } from "@mantine/notifications"
import { useDisclosure } from "@mantine/hooks"
import React, { useEffect } from "react";

type LoaderData = {
    projects: {
        name: string
    }[]
}

export const loader: LoaderFunction = async ({ }): Promise<LoaderData> => {
    const getProjectsResponse = await apiClient.GET("/api/projects")
    if (getProjectsResponse.error) {
        throw Response.error()
    }

    const projects = getProjectsResponse.data
        .map((project) => ({ name: project.name ?? "unknown/unknown" }))

    return {
        projects: projects
    }
}

type ActionData = {
    intent: "delete" | "add"
    success: boolean
}

const deleteFormDataSchema = z.object({
    intent: z.literal("delete"),
    id: z.int().min(1, "ID is required"),
})
const addFormDataSchema = z.object({
    intent: z.literal("add"),
    name: z.string().min(1, "Name is required"),
})
const actionFormDataSchema = z.union([deleteFormDataSchema, addFormDataSchema])

export const action: ActionFunction = async ({ request }): Promise<ActionData> => {
    const formData = await request.formData()
    const parsedFormData = actionFormDataSchema.safeParse(Object.fromEntries(formData))
    if (!parsedFormData.success) {
        console.error("Invalid form data", parsedFormData.error)
        throw new Response("Invalid form data", { status: 400 })
    }
    const { intent } = parsedFormData.data
    if (intent === "delete") {
        const { id } = parsedFormData.data
        const deleteProjectResponse = await apiClient.DELETE(`/api/projects/{id}`, {
            params: {
                path: {
                    id: id
                }
            }
        })

        if (deleteProjectResponse.error) {
            return {
                intent: "delete",
                success: false
            }
        }

        return {
            intent: "delete",
            success: true
        }
    } else if (intent === "add") {
        const { name } = parsedFormData.data

        const addProjectResponse = await apiClient.POST("/api/projects", {
            body: {
                name: name,
            }
        })

        if (addProjectResponse.error) {
            return {
                intent: "add",
                success: false
            }
        }

        return {
            intent: "add",
            success: true
        }
    }

    throw new Response("Invalid intent", { status: 400 })
}

const ProjectsPage: React.FC = () => {
    const { projects } = useLoaderData<LoaderData>()
    const actionData = useActionData<ActionData>()

    useEffect(() => {
        if (!actionData) {
            return
        }

        if (actionData.intent === "delete") {
            if (actionData.success) {
                notifications.show({
                    icon: <IconTrash size={16} />,
                    title: "Project deleted",
                    message: "Project has been deleted successfully.",
                    color: "red"
                })
            } else {
                notifications.show({
                    icon: <IconExclamationCircle size={16} />,
                    title: "Failed to delete project",
                    message: "An error occurred while deleting the project.",
                    color: "red"
                })
            }
        } else if (actionData.intent === "add") {
            if (actionData.success) {
                notifications.show({
                    icon: <IconPlus size={16} />,
                    title: "Project added",
                    message: "Project has been added successfully.",
                    color: "green"
                })
            } else {
                notifications.show({
                    icon: <IconExclamationCircle size={16} />,
                    title: "Failed to add project",
                    message: "An error occurred while adding the project.",
                    color: "red"
                })
            }
        }
    }, [actionData])

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
                        <TextInput name={"name"} label={"Name"} description={"Name of the project on GitHub (i.e. 'user/repo')."} placeholder={"Enter project name"} />
                        
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
                            return (
                                <Table.Tr key={project.name}>
                                    <Table.Td>
                                        <Anchor href={`https://github.com/${project.name}`}>{project.name}</Anchor>
                                    </Table.Td>
                                    <Table.Td>
                                        <Group>
                                            <Form method={"delete"}>
                                                <input type="hidden" name="intent" value="delete" />
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
