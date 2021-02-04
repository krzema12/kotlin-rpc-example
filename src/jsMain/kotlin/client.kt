import it.krzeminski.todoapp.api.Todo
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.li
import react.dom.render
import react.dom.ul
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import react.setState

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            browserRouter {
                switch {
                    route("/", exact = true) {
                        child(TodoApp::class) {}
                    }
                }
            }
        }
    }
}

external interface TodoAppState : RState {
    var todos: List<Todo>
}

class TodoApp : RComponent<RProps, TodoAppState>(), CoroutineScope by MainScope() {
    override fun TodoAppState.init() {
        todos = emptyList()
    }

    override fun componentDidMount() {
        fetchTodos()
    }

    override fun RBuilder.render() {
        h1 { +"TODOs:" }
        if (state.todos.isNotEmpty()) {
            ul {
                state.todos.forEach { todo ->
                    li {
                        +"${todo.description} (assignee: ${todo.assignee ?: "unassigned"}) - done: ${todo.isDone}"
                    }
                }
            }
        } else {
            div { +"No TODOs!" }
        }
    }

    private fun fetchTodos() {
        with(TodoAppApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            launch {
                val fetchedTodos = listTodos()
                setState {
                    todos = fetchedTodos
                }
            }
        }
    }
}
