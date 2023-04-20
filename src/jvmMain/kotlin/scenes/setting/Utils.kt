package scenes.setting

import model.Group
import model.Student

val block = "[\\p{Z}\\t]+".toRegex()

fun String.toGroups(): MutableList<Group> {
    val groups: MutableList<Group> = mutableListOf()
    split("[").forEach { groupString ->
        if (groupString != "") {
            groupString.split("]").also { groupStringList ->
                val groupName = groupStringList[0]
                require(groupName.isNotBlank())
                val membersSting = groupStringList[1]
                val groupMembers: MutableList<Student> = mutableListOf()
                membersSting.split("\n").forEach { memberString ->
                    if (memberString != "") {
                        memberString.split(block).also { memberStringList ->
                            val num = memberStringList[0].toLong()
                            val name = memberStringList[1]
                            val student = Student(num, name)
                            groupMembers.add(student)
                        }
                    }
                }
                val group = Group(groupName, groupMembers)
                groups.add(group)
            }
        }
    }
    return groups
}

fun String.checkToGroupsError(): Boolean {
    return try {
        toGroups()
        false
    } catch (_: Exception) {
        true
    }
}

/*fun List<Group>.formatToString(): String {
    var string = ""
    forEach { group ->
        string += "[${group.name}]\n"
        group.members.forEach { member ->
            string += "${member.num} ${member.name}\n"
        }
    }
    return string
}*/

fun String.toGroup(): Group {
    split("[").also {
        it[1].split("]").also { groupStringList ->
            val groupName = groupStringList[0]
            require(groupName.isNotBlank())
            val membersSting = groupStringList[1]
            val groupMembers: MutableList<Student> = mutableListOf()
            membersSting.split("\n").forEach { memberString ->
                if (memberString != "") {
                    memberString.split(block).also { memberStringList ->
                        val num = memberStringList[0].toLong()
                        val name = memberStringList[1]
                        val student = Student(num, name)
                        groupMembers.add(student)
                    }
                }
            }
            return Group(groupName, groupMembers)
        }
    }
}

fun String.checkToGroupError(): Boolean {
    return try {
        toGroup()
        false
    } catch (_: Exception) {
        true
    }
}

fun Group.formatToString(): String {
    var string = "[${name}]\n"
    members.forEach { member ->
        string += "${member.num} ${member.name}\n"
    }
    return string
}

fun String.replaceTabToSpace(): String {
    return replace("\t".toRegex(), " ")
}