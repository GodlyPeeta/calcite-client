package calcite.commons.interfaces

interface Alias : Nameable {
    val alias: Array<out String>
}