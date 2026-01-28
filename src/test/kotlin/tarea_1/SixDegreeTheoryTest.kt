package tarea_1

import isis4208.tarea_1.sixDegreeTheory
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SixDegreeTheoryTest  {
    @Test
    fun `should be successful`() {
        // ----------------------
        // SUCCESS CASE (true)
        // Index mapping (index -> id):
        // 0->101, 1->102, 2->103, 3->104, 4->105, 5->106, 6->107, 7->108, 8->109,
        // 9->110, 10->111, 11->112, 12->113, 13->114, 14->115, 15->116, 16->117, 17->118
        //  ** Data generated using Generative AI **
        //  And verified by me
        val usersSuccess: Array<IntArray> = arrayOf(
            intArrayOf(1, 2, 3, 4, 5),     // 0 (101)
            intArrayOf(0, 2, 6, 7),        // 1 (102)
            intArrayOf(0, 1, 3, 8),        // 2 (103)
            intArrayOf(0, 2, 4, 9),        // 3 (104)
            intArrayOf(0, 3, 5, 10),       // 4 (105)
            intArrayOf(0, 4, 1, 11),       // 5 (106)
            intArrayOf(1, 12),             // 6 (107)
            intArrayOf(1, 13),             // 7 (108)
            intArrayOf(2, 14),             // 8 (109)
            intArrayOf(3, 15),             // 9 (110)
            intArrayOf(4, 16),             // 10 (111)
            intArrayOf(5, 17),             // 11 (112)
            intArrayOf(6, 0),              // 12 (113)
            intArrayOf(7, 0),              // 13 (114)
            intArrayOf(8, 0),              // 14 (115)
            intArrayOf(9, 0),              // 15 (116)
            intArrayOf(10, 0),             // 16 (117)
            intArrayOf(11, 0)              // 17 (118)
        )
        assertTrue(sixDegreeTheory(usersSuccess), "Should return true")
    }

    @Test
    fun `Should fail`(){
        // ----------------------
        // FAIL CASE (false)
        // Index mapping (index -> id):
        // 0->201, 1->202, 2->203, 3->204, 4->205, 5->206, 6->207, 7->208
        //  ** Data generated using Generative AI **
        //  And verified by me
        val usersFail: Array<IntArray> = arrayOf(
            intArrayOf(1),                 // 0 (201)
            intArrayOf(0, 2),              // 1 (202)
            intArrayOf(1, 3),              // 2 (203)
            intArrayOf(2, 4),              // 3 (204)
            intArrayOf(3, 5),              // 4 (205)
            intArrayOf(4, 6),              // 5 (206)
            intArrayOf(5, 7),              // 6 (207)
            intArrayOf(6)                  // 7 (208)
        )
        assertFalse(sixDegreeTheory(usersFail), "Should return true")
    }

}