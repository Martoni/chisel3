// See LICENSE for license details.

package chiselTests

import chisel3._
import chisel3.util.ChiselLoadMemoryAnnotation
import org.scalatest.{FreeSpec, Matchers}

//noinspection TypeAnnotation
class UsesMem(memoryDepth: Int, memoryType: Data) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(memoryType.getWidth.W))
    val value   = Output(memoryType)
    val value2  = Output(memoryType)
  })

  val memory = Mem(memoryDepth, memoryType)

  chisel3.experimental.annotate(ChiselLoadMemoryAnnotation(memory, "./mem1.txt"))
  io.value := memory(io.address)

  val low = Module(new UsesMemLow(memoryDepth, memoryType))

  low.io.address := io.address
  io.value2 := low.io.value
}

class UsesMemLow(memoryDepth: Int, memoryType: Data) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(memoryType.getWidth.W))
    val value   = Output(memoryType)
  })

  val memory = Mem(memoryDepth, memoryType)

  chisel3.experimental.annotate(ChiselLoadMemoryAnnotation(memory, "./mem2.txt"))
  io.value := memory(io.address)
}


class LoadMemoryFromFileSpec extends FreeSpec with Matchers {
  "Users can specify a source file to load memory from" in {
    Driver.execute(
      args = Array("-X", "verilog", "--target-dir", "test_run_dir"),
      dut = () => new UsesMem(memoryDepth = 8, memoryType = UInt(16.W))
    )
  }
}