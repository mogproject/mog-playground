package com.mogproject.mogami.playground.model

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.Message
import com.mogproject.mogami.frontend.model.board.cursor.Cursor


/**
  *
  */
case class PlaygroundModel(override val mode: Mode,
                     override val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                     override val activeCursor: Option[(Int, Cursor)] = None,
                     override val selectedCursor: Option[(Int, Cursor)] = None,
                     override val flashedCursor: Option[Cursor] = None,
                     override val menuDialogOpen: Boolean = false,
                     override val messageBox: Option[Message] = None
                    ) extends BasePlaygroundModel(mode, config, activeCursor, selectedCursor, flashedCursor, menuDialogOpen, messageBox) {

}

// todo: TestModel(baseModel: BasePlaygroundModel)
object PlaygroundModel {
  def adapter(m: PlaygroundModel, bm: BasePlaygroundModel): PlaygroundModel = {
    PlaygroundModel(bm.mode, bm.config, bm.activeCursor, bm.selectedCursor, bm.flashedCursor, bm.menuDialogOpen, bm.messageBox)
  }
}