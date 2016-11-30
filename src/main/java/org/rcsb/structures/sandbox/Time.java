//package org.rcsb.structures.sandbox;
//
//import java.util.List;
//
//import org.biojava.nbio.alignment.Alignments;
//import org.biojava.nbio.alignment.SimpleGapPenalty;
//import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
//import org.biojava.nbio.alignment.template.GapPenalty;
//import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
//import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
//import org.biojava.nbio.core.alignment.template.SequencePair;
//import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
//import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
//import org.biojava.nbio.core.sequence.ProteinSequence;
//import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
//import org.biojava.nbio.structure.StructureException;
//import org.biojava.nbio.structure.align.seq.SmithWaterman3DParameters;
//import org.rcsb.sequences.LongestCommonSubsequence;
//import org.rcsb.sequences.LongestCommonSubstring;
//
//public class Time {
//	
//	public static void main(String[] args) throws StructureException {
//		
//		String s1 = "MKKVERFEVPRTIIFGPGALEKTPEVIPPSGRVLIITGKSSTRKYAERVAELLKQNCEIISYDQVELEKPGFDLVIGIGGGRPLDMAKVYSYIHKKPFVAIPTSASHDGIASPYVSFSLTQRFSKYGKISSSPVAIIADTSIILSAPSRLLKAGIGDLLGKIIAVRDWQLAHRLKGEEYSEYAAHLSLTSYKIAVGNAQKIKNFIREEDVRVLVKALIGCGVAMGIAGSSRPCSGSEHLFAHAIEVRVEKEDEVVHGELVALGTIIMAYLHGINWRRIKRIADIIGLPTSLRQANIDVDLALEALTTAHTLRPDRYTILGDGLSREAAKRALEDVELI";
//		String s2 = "MKKVERFEVPRTIIFGPGALEKTPEVIPPSGRFFVLIITGKSSTRKYAERVAELLKQNCEIISYDQVKELEKPGFDLVIGIGGGRPLDMAKVYSYIHKKPFVAIPTSASHDGIASPYVSFSLTQRFSKYGKISSSPVAIIADTSIILSAPSRLLKAGIGDLLGKIIAVRDWQLAHRLKGEEYSEYAAHLSLTSYKIAVGNAQKIKNFIREEDVRVLVKALIGCGVAMGIAGSSRPCSGSEHLFAHAIEVRVEKEDEVVHGELVALGTIIMAYLHGINWRRIKRIADIIGLPTSLRQANIDVDLALEALTTAHTLRPDRYTILGDGLSREAAKRALEDVELI";
//		
//		System.out.println("Length string 1: "+s1.length());
//		System.out.println("Length string 2: "+s2.length());
//		
//		LongestCommonSubstring test1 = new LongestCommonSubstring();
//		long start = System.nanoTime();
//		List<Integer> res1 = test1.longestCommonSubstring(s1, s2);
//		System.out.println("Alignment length (1): "+(res1.get(1)-res1.get(0)));
//		long end = System.nanoTime();
//		System.out.println("Time: " + (end-start)/1E9+"(s)");
//		
//		LongestCommonSubsequence test2 = new LongestCommonSubsequence(s1, s2);
//		long start1 = System.nanoTime();
//		int[][] res2 = test2.alignSequences();
//		System.out.println("Alignment length (2): "+res2.length);
//		long end1 = System.nanoTime();
//		System.out.println("Time: " + (end1-start1)/1E9+"(s)");
//		
//		// 
//		long start2 = System.nanoTime();
//		System.out.println("Smith Waterman");
//		
//		ProteinSequence ps1 = null;
//		ProteinSequence ps2 = null;
//		try {
//			ps1 = new ProteinSequence(s1);
//			ps2 = new ProteinSequence(s2);
//		} catch (CompoundNotFoundException e) { throw new StructureException(e.getMessage(),e); }
//		
//		// default blosum62
//		SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum65();
//
//		GapPenalty penalty = new SimpleGapPenalty();
//		
//		SmithWaterman3DParameters params = new SmithWaterman3DParameters();
//		penalty.setOpenPenalty(params.getGapOpen());
//		penalty.setExtensionPenalty(params.getGapExtend());
//
//		PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> smithWaterman =
//			Alignments.getPairwiseAligner(ps1, ps2, PairwiseSequenceAlignerType.LOCAL, penalty, matrix);
//
//		SequencePair<ProteinSequence, AminoAcidCompound> pair = smithWaterman.getPair();
//		
//		long end2 = System.nanoTime();
//		System.out.println("Time: " + (end2-start2)/1E9+"(s)");
//		
//	}
//
//}
