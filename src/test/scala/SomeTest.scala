import org.scalatest.flatspec.AnyFlatSpec

class SomeTest extends AnyFlatSpec {

  behavior of "Some Test"

  it should "assert True" in {
    val x = 2 + 3
    assert(5 == x)
  }

}
