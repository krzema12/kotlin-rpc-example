import it.krzeminski.todoapp.api.Todo
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.input
import react.dom.render
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

    override fun RBuilder.render() {
        div {
            +"From backend: ${state.todos}"
        }
        input(type = InputType.button) {
            attrs {
                value = "Fetch TODOs"
                onClickFunction = {
                    launch {
                        fetchTodos()
                    }
                }
            }
        }
    }

    private suspend fun fetchTodos() {
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
