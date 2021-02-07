import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import it.krzeminski.todoapp.api.Todo
import it.krzeminski.todoapp.api.TodoAppApi
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

fun main() {
    val todos = mutableListOf(
        Todo(
            description = "Initial task",
            isDone = false,
        ),
    )

    val todoAppApiImpl = object : TodoAppApi {
        override suspend fun listTodos() = todos

        override suspend fun addToList(description: String): List<Todo> {
            todos.add(Todo(
                description = description,
                isDone = false,
            ))
            return todos
        }
    }

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") { webApp() }
            todoAppApiKtorHandlers(todoAppApiImpl)
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.webApp() {
    call.respondHtml(HttpStatusCode.OK) {
        head {
            title("TODO!")
        }
        body {
            div {
                id = "root"
            }
            script(src = "/static/todoapp.js") {}
        }
    }
}
