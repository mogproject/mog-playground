package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.core.{Game, MoveBuilderSfen, Square, State}
import com.mogproject.mogami.playground.controller.mode.Viewing
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class ArgumentsSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {
  "Arguments#parseQueryString" must "create configuration" in {
    Arguments().parseQueryString("") mustBe Arguments()
    Arguments().parseQueryString("?mode=view&lang=en") mustBe Arguments(config = Configuration(mode = Viewing, lang = English))
    Arguments().parseQueryString("?sfen=lnsgkgsnl%2F1r5b1%2Fppppppppp%2F9%2F9%2F9%2FPPPPPPPPP%2F1B5R1%2FLNSGKGSNL%20b%20-%200%207g7f&mode=view&lang=en") mustBe Arguments(
      game = Game(State.HIRATE).makeMove(MoveBuilderSfen(Left(Square(7, 7)), Square(7, 6), promote = false)).get,
      config = Configuration(mode = Viewing, lang = English)
    )

  }

}
