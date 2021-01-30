package com.myunidays.udb.cli

import com.myunidays.udb.mainBlock

fun main(args: Array<String>) = mainBlock {
    ApplicationArgParser(rawArgs = args).parse(args)
}
