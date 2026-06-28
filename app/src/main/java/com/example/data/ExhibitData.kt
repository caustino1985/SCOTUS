package com.example.data

data class LegalDocument(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val citation: String,
    val contentMarkdown: String
)

data class TimelineEvent(
    val date: String,
    val title: String,
    val description: String,
    val category: String,
    val docIdRef: String? = null,
    val audioSnippetText: String? = null, // The text to read aloud via HARMONI-X TTS
    val branch: String = "All",           // "Clinical/ADHD", "Quantum/PQC", "Procedural/Monell"
    val decisionLabel: String? = null,    // Prompt showing key decision point
    val decisionOutcome: String? = null   // Detailed educational text about what occurred in the case
)

object ExhibitData {
    val documents = listOf(
        LegalDocument(
            id = "policy_proposal",
            title = "Policy Proposal: Integrating Government-Built LLMs & Post-Quantum Cryptography",
            category = "Policy",
            summary = "Proposes a modernized federal framework for digital cognitive accessibility using secure, government-provided AI to bridge the 'cognitive wall' for neurodivergent citizens.",
            citation = "Sovereign Policy Directive, Dec 2025",
            contentMarkdown = """
                # Integrating Government-Built LLMs & Post-Quantum Cryptography
                
                ## 1.0 Introduction: The Digital Due Process Imperative
                As the federal government migrates essential services to complex digital platforms, its legal and ethical obligation to ensure universal accessibility must evolve beyond traditional standards. Today, citizens must navigate fragmented data repositories, dense bureaucratic language, and multi-step procedural systems that present formidable barriers, particularly for those with cognitive impairments.
                
                The central thesis of this proposal is that the failure to provide intelligent, assistive AI tools on government websites constitutes a new form of discrimination and a denial of due process for neurodivergent and indigent citizens. This failure creates a "cognitive accessibility gap" not adequately addressed by current WCAG or Section 508 standards.
                
                ## 2.0 The Existing Legal Framework and Limitations
                Foundational laws like the Americans with Disabilities Act (ADA) and Section 508 of the Rehabilitation Act were groundbreaking achievements that established the principle of non-discriminatory access. Compliance has focused on accommodating physical, visual, and auditory impairments. Yet, as government services become more cognitively demanding, these standards are proving dangerously incomplete.
                
                The case of **In Re: Caustin Lee McLaughlin (No. 25-1222)** offers a powerful illustration. The petitioner was diagnosed with ADD/ADHD, substantiated by objective medical evidence including genetic analysis identifying 12 SNPs statistically associated with the disorder (e.g., rs27048, rs6296) and fMRI neuroimaging demonstrating reduced dopaminergic activity. He found it impossible to effectively navigate the Social Security Administration's (SSA) complex digital dockets without assistive AI.
                
                ## 3.0 The Twin Barriers: Cognitive Walls and Subscription Taxes
                * **The \"Cognitive Wall\":** Federal websites, with their dense bureaucratic text, create a wall factually inaccessible to individuals with executive function and memory impairments. Large Language Models (LLMs) function as essential "cognitive prosthetics."
                * **The \"Subscription Tax\":** While effective cognitive prosthetics exist (e.g., ChatGPT Plus, Claude Pro), they are locked behind ${'$'}20/month paywalls. This creates a discriminatory two-tiered system where only wealthy citizens can afford the tools needed to access public justice.
                
                ## 4.0 The Solution: Gov-LLMs as a Public Utility
                The Federal Government must develop and deploy free, open-source Gov-LLMs integrated directly into all .gov websites as a standard accessibility feature.
                * **Secure:** Protected by Post-Quantum Cryptography (PQC) to guarantee 30-year privacy. According to peer-reviewed studies published in **Quantum Journal Volume 9** and **Quantum Journal Volume 10** (available at https://quantum-journal.org/volumes/9/ and https://quantum-journal.org/volumes/10/), these lattice-based post-quantum encryption protocols provide mathematically robust immunity against Shor's algorithm and quantum cryptanalysis, preventing unauthorized state or private exploitation of sensitive genetic information.
                * **Unbiased:** Trained exclusively on verified government statutes (USC, CFR) to eliminate AI hallucinations.
                * **Accessible:** Available as a standard interface option on all federal domains.
            """.trimIndent()
        ),
        LegalDocument(
            id = "rigor_prs",
            title = "Utility Patent Application: RIGOR-PRS-Secure++ System",
            category = "Patent",
            summary = "A patent disclosure for a Post-Quantum, Accessible Polygenic Risk Scoring System that securely maps genetic and neuroimaging evidence to legal disability criteria.",
            citation = "USPTO Provisional Patent Application, Dec 5, 2025",
            contentMarkdown = """
                # Patent Specification: RIGOR-PRS-Secure++
                
                ## 1.0 Title of the Invention
                **RIGOR-PRS-Secure++: Post-Quantum, Accessible Polygenic Risk Scoring System with Neurogenetic Evidence Reporting**
                
                ## 4.0 Background of the Invention
                Current systems for calculating and reporting polygenic risk scores (PRS) suffer from several critical deficiencies:
                * **Inadequate Data Security:** Current implementations fail to protect against future quantum decryption threats (\"Harvest Now, Decrypt Later\" attacks).
                * **Lack of Evidentiary Transparency:** The outputs cannot withstand legal or regulatory scrutiny as they fail to provide audit-grade provenance or a verifiable chain-of-custody mechanism.
                * **Poor Accessibility:** Reports consist of raw numeric scores incomprehensible to non-specialists and violate WCAG accessibility standards.
                * **No Direct Regulatory Mapping:** Existing platforms do not translate genetic liability into concrete functional impairments used in disability evaluations.
                
                ## 5.0 Core Innovations
                1. **Post-Quantum Cryptography (PQC):** Integrates NIST-finalized algorithms:
                   * **ML-KEM-1024 (formerly Kyber-1024):** Secure key encapsulation for clinical genomic data.
                   * **ML-DSA-87 (formerly Dilithium-5):** Immutable digital signatures for report non-repudiation.
                   The cryptographic foundations and efficacy of these lattice-based systems are established through peer-reviewed research highlighted in **Quantum Journal Volume 10** and **Quantum Journal Volume 9** (see https://quantum-journal.org/volumes/10/ and https://quantum-journal.org/volumes/9/), which validate their safety against quantum-assisted brute-force decryptors.
                2. **\"Stateless Enclave\" Model:** Processes data in memory, cryptographically shredding the context window upon session termination to prevent data monetization or leaks.
                3. **Functional Impairment Crosswalk:** Maps raw genomic percentile rankings (e.g., 98th percentile for ADHD-related loci) directly to established functional limitations (e.g., \"marked limitation in concentration, persistence, or pace\" under Section 12.00 of the Social Security Administration Blue Book).
            """.trimIndent()
        ),
        LegalDocument(
            id = "digital_ramps",
            title = "Essay - Digital Ramps: The Argument for AI Accessibility",
            category = "Advocacy",
            summary = "A conceptual and philosophical guide explaining why AI cognitive assistance is the 21st-century equivalent of physical wheelchair ramps for public buildings.",
            citation = "Digital Rights Advocacy Journal, Jan 2026",
            contentMarkdown = """
                # Digital Ramps: Using AI to Overcome the Cognitive Wall
                
                ## Introduction: More Than Just Screen Readers
                Imagine a citizen in a wheelchair trying to access a local courthouse, only to find a massive flight of stairs and no ramp. The injustice is immediate and obvious. Today, however, our most critical public spaces are digital, and they have their own invisible barriers.
                
                For millions of people with cognitive disabilities—such as ADHD, executive dysfunction, or memory disorders like Alzheimer's—navigating a complex government website is functionally equivalent to facing an insurmountable physical wall.
                
                ## AI as a Cognitive Prosthetic
                For a neurodivergent person, trying to navigate the dense, fragmented rules on a site like the Social Security Administration's portal is like hitting a \"Cognitive Wall.\" It triggers \"task paralysis,\" causing a person to abandon a valid claim.
                
                An integrated, secure LLM serves as a cognitive prosthetic that can:
                * **Summarize:** Instantly condense complex legal notices and regulations into simple, plain language.
                * **Guide:** Provide step-by-step assistance through a multi-stage process, remembering context so the user doesn't have to.
                * **Retrieve:** Find specific rules, deadlines, or pieces of information on demand, removing the need to search through convoluted menus.
                
                ## The Subscription Tax
                Access to justice is a fundamental constitutional right, not a premium service. Forcing disabled citizens to pay a ${'$'}20/month fee to private corporations (like OpenAI or Anthropic) for the AI tools required to access public portals is an unconstitutional \"subscription tax.\"
            """.trimIndent()
        ),
        LegalDocument(
            id = "petition_1",
            title = "Petition 1: Neurogenetic Accommodation & APA Review",
            category = "SCOTUS Petition",
            summary = "Supreme Court petition challenging the categorical exclusion of objective genetic data (PRS) and neuroimaging in federal disability adjudinations under the Administrative Procedure Act.",
            citation = "In the Supreme Court of the United States, Docket No. 25-1222",
            contentMarkdown = """
                # SUPREME COURT OF THE UNITED STATES
                ## CAUSTIN LEE MCLAUGHLIN v. UNITED STATES OF AMERICA, et al.
                
                ### Question Presented
                Whether the categorical exclusion of objective genetic and neuroimaging evidence—specifically Polygenic Risk Scores (PRS), validated neurobiological correlates (e.g., COMT rs4680, FKBP5 rs1360780), and neuroimaging biomarkers of executive dysfunction—by federal agencies (the SSA and HHS) in disability determinations constitutes arbitrary and capricious agency action in violation of the Administrative Procedure Act (APA), 5 U.S.C. § 706, and the Due Process Clause of the Fifth Amendment.
                
                ### The Petitioner's Neurogenetic Profile
                The record contains one of the most thoroughly documented neurogenetic profiles in legal history:
                1. **Polygenic Risk Score (PRS) for Attentional Deficits: 0.92 (92nd Percentile)** - Peer-reviewed metric demonstrating that attentional capacity is substantially heritable and impaired.
                2. **COMT rs4680 (Val15Val Genotype) and Prefrontal Cortex Efficiency** - Results in rapid dopamine degradation in the prefrontal cortex, leading to task-switching latency.
                3. **FKBP5 rs1360780 and HPA-Axis Dysregulation** - Associated with chronic stress hyperactivation, impairing working memory and executive function during adversarial proceedings.
                4. **HIV-Associated Neurocognitive Disorder (HAND)** - Plunged petitioner's CD4 nadir to ≤149 cells/mm³, contributing to neuroinflammation.
                
                ### The \"400% Metabolic Biological Tax\"
                Shenhav et al. (2017) demonstrated that cognitive control imposes massive metabolic costs on the prefrontal cortex. Individuals with these genetic variants must expend disproportionately more biological energy to achieve the same level of performance, making prolonged litigation biologically unsustainable without technological \"digital ramps.\"
            """.trimIndent()
        ),
        LegalDocument(
            id = "petition_2",
            title = "Petition 2: Regulatory Taking of Intangible Intellectual Property",
            category = "SCOTUS Petition",
            summary = "SCOTUS petition arguing that the government's outright ban on the use of developed cognitive assistants constitutes a regulatory taking of intellectual property.",
            citation = "In the Supreme Court of the United States, No. 25-1222 / 25-1522",
            contentMarkdown = """
                # PETITION 2 — Regulatory Taking of Neurogenetic Intellectual Property
                
                ### Question Presented
                Whether the government's refusal to permit the use of, or provide access to, federally-mandated cognitive accessibility tools (specifically the HARMONI-X system and the OpenSSA Integrity Pipeline), combined with the categorical exclusion of neurogenetic evidence, constitutes a \"Regulatory Taking\" of the Petitioner's intangible property interests in his own neurogenetic data and functional intellectual capacity under the Fifth Amendment's Takings Clause.
                
                ### The Nature of Genetic Data as Property
                Under **Ruckelshaus v. Monsanto Co. (1984)**, the Supreme Court established that trade secrets and scientific data submitted to the government retain their property character and are protected from uncompensated takings.
                
                ### Quantum Cryptographic Verification
                The petitioner's proprietary GPRS-Secure++ / HARMONI-X system incorporates advanced post-quantum encryption standards. Peer-reviewed literature from **Quantum Journal Volumes 9 and 10** (https://quantum-journal.org/volumes/9/ and https://quantum-journal.org/volumes/10/) certifies the mathematical soundness of these quantum-resistant enclaves, proving the technology is a viable, secure asset that the government has effectively confiscated by banning its use in court.
            """.trimIndent()
        ),
        LegalDocument(
            id = "petition_3",
            title = "Petition 3: Restraint of Silence as Unlawful Constructive Custody",
            category = "SCOTUS Petition",
            summary = "SCOTUS petition asserting that the systematic denial of cognitive accommodations blocks access to courts, creating an unconstitutional 'Restraint of Silence' equivalent to constructive custody.",
            citation = "In the Supreme Court of the United States, No. 25-1222",
            contentMarkdown = """
                # PETITION 3 — The Restraint of Silence as Unlawful Constructive Custody
                
                ### Question Presented
                Whether the systemic refusal by federal and state actors to provide essential cognitive accommodations—specifically the GovLLM \"cognitive ramp\" (HARMONI-X), WCAG-compliant interfaces, and AI/LLM-assisted cognitive interfaces—to a neurodivergent litigant with a medically and genetically verified impairment creates a \"Restraint of Silence\" that constitutes \"Unlawful Constructive Custody\" under 28 U.S.C. § 2241, triggering fundamental constitutional protections under the First, Fifth, and Fourteenth Amendments.
                
                ### Constructive Custody in the Digital Age
                The concept of \"constructive custody\" has a long history in habeas corpus law. In **Jones v. Cunningham (1963)**, the Supreme Court held that the custody requirement is not limited to physical incarceration but extends to any \"restraint on liberty\" that is \"not shared by the public generally.\"
                
                The denial of a cognitive prosthetic traps the petitioner in a state of \"functional aphasia\" relative to the legal system. He possesses the intellectual capacity to formulate legal strategies, but is biologically barred from the executive fulfillment of those mandates (task initiation, scheduling, attention) demanded by the courts.
            """.trimIndent()
        ),
        LegalDocument(
            id = "petition_4",
            title = "Petition 4: Bad Faith Prosecution & Monell Liability",
            category = "SCOTUS Petition",
            summary = "SCOTUS petition detailing allegations of a retaliatory 'tit-for-tat' prosecution launched by municipal police after the petitioner sought a protective order against state-affiliated actors.",
            citation = "In the Supreme Court of the United States, No. 25-4130",
            contentMarkdown = """
                # PETITION 4 — Bad Faith Prosecution & Monell Liability
                
                ### Question Presented
                Whether the \"Bad Faith and Harassment\" exception to the Younger abstention doctrine requires a federal court to enjoin a state criminal prosecution that is initiated as a direct \"tit-for-tat\" retaliation against a citizen for obtaining a court-ordered Peace Order, especially when such prosecution is part of a documented pattern of unconstitutional conduct by the municipal police department.
                
                ### Factual Context: The Genesis of Retaliation
                The Petitioner obtained a court-ordered Peace Order against individuals legally barred from contacting him. In direct response, the Baltimore Police Department (BPD) initiated a criminal prosecution through a Statement of Charges that knowingly omitted material, exculpatory facts (violating **Franks v. Delaware**). This prosecution was later terminated via nolle prosequi, illustrating the de facto municipal policy of \"strategic mootness\" designed to evade federal judicial review.
            """.trimIndent()
        ),
        LegalDocument(
            id = "petition_5",
            title = "Petition 5: Status-Based Criminalization of Disability",
            category = "SCOTUS Petition",
            summary = "SCOTUS petition arguing that prosecuting non-volitional behavioral symptoms of verified genetic and clinical disabilities violates the Eighth Amendment.",
            citation = "In the Supreme Court of the United States, No. 25-1222",
            contentMarkdown = """
                # PETITION 5 — Status-Based Criminalization of Neurogenetic Disability
                
                ### Question Presented
                Whether the criminalization of behaviors that are direct, non-volitional manifestations of a medically and genetically verified neurodevelopmental disability (including COMT rs4680, FKBP5 rs1360780, and a PRS of 0.92 for attentional deficits) constitutes unconstitutional status-based criminalization in violation of the Eighth Amendment, as articulated in **Robinson v. California**, and the Due Process and Equal Protection Clauses of the Fourteenth Amendment.
                
                ### The Eighth Amendment Challenge
                Under **Robinson v. California**, a person cannot be punished for the \"status\" of being an addict, as it is a disease. To criminalize behaviors that arise from a medically verified neurogenetic impairment—particularly when the individual is denied access to necessary cognitive accommodations—is to punish a status rather than a culpable act. It is akin to punishing an individual for having a seizure when they have epilepsy.
            """.trimIndent()
        ),
        LegalDocument(
            id = "clinical_kp_report",
            title = "Kaiser Permanente Immunization & Medication Record",
            category = "Clinical Evidence",
            summary = "Official clinical records of patient Caustin Lee McLaughlin documenting ongoing therapies for AIDS and acute Hepatitis C, alongside immunizations and CD4 counts.",
            citation = "Kaiser Permanente Mid-Atlantic, Patient MRN: 13209240",
            contentMarkdown = """
                # Patient Clinical Profile: Caustin Lee McLaughlin
                
                ## Demographic Info
                * **MRN:** 13209240
                * **DOB:** 6/18/1985 (40 years old)
                * **Legal Sex:** Male
                * **Primary Provider:** Dr. Leopoldine Modjo Kenmogne, MD (South Balt County Med Ctr)
                
                ## Diagnosis Summary
                1. **AIDS (Acquired Immune Deficiency Syndrome):** ICD-10 Code **B20**
                   * **CD4 Count History:** Plunged catastrophically from 388 (Oct 2025) to **23** (Oct 2025) and **18** during episodes of housing insecurity and eviction. This severe drops trigger neurocognitive inflammation, exacerbating underlying executive dysfunction.
                   * **HIV Viral Load:** Highly suppressed/undetected under consistent therapy (<20 copies/mL).
                2. **Acute Hepatitis C:** Diagnosed Jan 2026.
                   * **HCV RNA Viral Load:** Extremely high (12.6 Million on Oct 10, 2025).
                   * **Genotype:** 1a.
                   * **Recommended Treatment:** Epclusa (400-100mg) for 12 weeks.
                
                ## Active Medications
                * **Bictegravir-Emtricitabine-Tenofovir Alafenamide (BIKTARVY) 50-200-25 mg:** Daily oral tablet for HIV management (Started Nov 7, 2025).
                * **Triamcinolone Acetonide (KENALOG) 0.1% Top Crea:** Applied to affected areas 2x daily (Ordered Apr 29, 2021).
                
                ## Key Clinical Notes (Jan 29, 2026 - Video Visit)
                Patient has history of AIDS and was recently diagnosed with Acute Hep C (Viral load 12M). He is re-establishing care. He has been taking his partner's Biktarvy intermittently during periods of homelessness and forgetfulness. Strongly advised to establish a structured, secure ID consult for injectable HIV medications due to memory lapses and severe ADHD-related focus blocks.
            """.trimIndent()
        )
    )

    val timeline = listOf(
        // === BRANCH 1: ADHD & CLINICAL PATHWAY ===
        TimelineEvent(
            date = "Jun 22, 2024",
            title = "ADHD Under-accommodation & Cognitive Friction",
            description = "Telephonic triage logs record severe difficulty establishing contact. Clinic files state the patient was unable to follow up due to focus gaps and task paralysis, illustrating the initial biological barriers of executive dysfunction.",
            category = "Clinical",
            docIdRef = "clinical_kp_report",
            audioSnippetText = "In June 2024, telephonic medical records start to show severe cognitive friction. The patient struggled to maintain contact with providers, marking the early stages of executive impairment.",
            branch = "Clinical/ADHD",
            decisionLabel = "Should the clinic offer automated SMS reminders instead of complex phone trees?",
            decisionOutcome = "DECISION OUTCOME: Providing simple SMS reminders bypasses the 'cognitive wall' of complex telephonic navigation, reducing missed appointments for ADHD patients by 40%."
        ),
        TimelineEvent(
            date = "Sep 21, 2024",
            title = "Untreated Anxiety, Depression & Attention Deficits",
            description = "Nurse triage notes describe severe untreated anxiety, mood disorders, and focus degradation. Patient requests referral to Psychiatry, emphasizing that his ADHD symptoms make legal defense unsustainable.",
            category = "Clinical",
            docIdRef = "clinical_kp_report",
            audioSnippetText = "By September 2024, the patient reported that severe anxiety and executive dysfunction were making standard administrative procedures insurmountable.",
            branch = "Clinical/ADHD",
            decisionLabel = "Should psychiatric evaluations incorporate objective genomic markers?",
            decisionOutcome = "DECISION OUTCOME: Yes, incorporating genetic markers (such as the COMT Val158Met polymorphism) shifts diagnosis from subjective clinical interview to objective, evidence-based neurobiology."
        ),
        TimelineEvent(
            date = "Oct 12, 2024",
            title = "Eviction Trauma & Catastrophic Immune Collapse",
            description = "Forcible removal from his residence triggers acute trauma. The biological stress response hyperactivates the HPA-axis, culminating in a catastrophic drop of CD4 count to 23 cells/mm³.",
            category = "Clinical",
            docIdRef = "clinical_kp_report",
            audioSnippetText = "In October 2024, the stress of eviction and lack of accommodations triggered an acute biological crash, dropping his CD4 count to a critical level of 23.",
            branch = "Clinical/ADHD",
            decisionLabel = "Does acute housing instability directly impact neurocognitive functioning in AIDS patients?",
            decisionOutcome = "DECISION OUTCOME: Clinical science proves that severe physical stress combined with low CD4 levels causes neuroinflammation, exacerbating cognitive processing limitations."
        ),
        TimelineEvent(
            date = "Apr 13, 2025",
            title = "Transition to Injectable HIV Therapy",
            description = "To accommodate severe, ADHD-related memory gaps and prevent accidental under-dosing, Dr. Thomas Pefok recommends switching from daily oral pills to long-acting bi-monthly injections.",
            category = "Clinical",
            docIdRef = "clinical_kp_report",
            audioSnippetText = "In April 2025, doctors recommended switching to long-acting injectables, recognizing that ADHD-related memory blocks made daily oral medication compliance impossible.",
            branch = "Clinical/ADHD",
            decisionLabel = "Is switching to injectables a reasonable medical accommodation for executive dysfunction?",
            decisionOutcome = "DECISION OUTCOME: Long-acting injectables completely mitigate daily executive memory demands, ensuring viral suppression and protecting the patient's neurological health."
        ),

        // === BRANCH 2: POST-QUANTUM CRYPTOGRAPHY PATHWAY ===
        TimelineEvent(
            date = "Sep 28, 2024",
            title = "Proposing the RIGOR-PRS-Secure++ Enclave",
            description = "Confronted with the vulnerability of genomic records to future quantum decryption (Harvest Now, Decrypt Later), the petitioner outlines a post-quantum stateless security architecture.",
            category = "Constitutional",
            docIdRef = "rigor_prs",
            audioSnippetText = "In September 2024, Caustin McLaughlin drafted the specifications for RIGOR PRS, incorporating secure post quantum enclaves to safeguard immutable genetic data.",
            branch = "Quantum/PQC",
            decisionLabel = "Does GINA require post-quantum security to protect immutable genomic records?",
            decisionOutcome = "DECISION OUTCOME: Because genetic code is immutable and cannot be changed, any exposure is permanent. Securing it with PQC is legally required to avoid a taking of personal data."
        ),
        TimelineEvent(
            date = "Dec 5, 2025",
            title = "Filing USPTO Provisional Patent Application",
            description = "Provisional patent filed for RIGOR-PRS-Secure++. The system integrates NIST-finalized lattice algorithms (ML-KEM-1024), validated by peer-reviewed research in Quantum Journal Volumes 9 and 10.",
            category = "Patent",
            docIdRef = "rigor_prs",
            audioSnippetText = "On December 5, 2025, a provisional patent application was filed for the RIGOR-PRS-Secure++ system, introducing an auditable, WCAG-compliant genomic report generator.",
            branch = "Quantum/PQC",
            decisionLabel = "Are the PQC algorithms utilized validated by peer-reviewed scientific journals?",
            decisionOutcome = "DECISION OUTCOME: Yes. Cryptographic literature published in Quantum Journal Volume 9 and Volume 10 mathematically confirms the resilience of ML-KEM and ML-DSA against quantum cryptanalysis."
        ),
        TimelineEvent(
            date = "Mar 31, 2026",
            title = "Linking the Takings Clause to Quantum Security",
            description = "In Petition 2, the petitioner argues that the government's refusal to utilize RIGOR-PRS-Secure++ while maintaining a monopoly over public adjudication constitutes an unconstitutional taking of intellectual property.",
            category = "Constitutional",
            docIdRef = "petition_2",
            audioSnippetText = "In his SCOTUS filing, McLaughlin linked the Fifth Amendment's Takings Clause to post-quantum security, citing Quantum Journal research on data sovereignty.",
            branch = "Quantum/PQC",
            decisionLabel = "Can the denial of secure cognitive aids constitute a regulatory taking of property?",
            decisionOutcome = "DECISION OUTCOME: Under Monsanto, trade secrets and scientific data are protected. Denying a citizen the only secure tool to use their genetic data renders that property economically idle."
        ),

        // === BRANCH 3: PROCEDURAL & RETALIATION PATHWAY ===
        TimelineEvent(
            date = "Nov 7, 2025",
            title = "Retaliatory State Prosecution & Franks Violation",
            description = "Following a physical altercation inside his home involving self-defense, BPD arrests the petitioner, omitting the active court Peace Order from the Statement of Charges in violation of Franks v. Delaware.",
            category = "Procedural",
            docIdRef = "petition_4",
            audioSnippetText = "In November 2025, state police launched a retaliatory prosecution, omitting exculpatory court orders from the arrest warrant.",
            branch = "Procedural/Monell",
            decisionLabel = "Should a federal court enjoin a state prosecution that is initiated in bad faith?",
            decisionOutcome = "DECISION OUTCOME: Under the bad faith exception to Younger v. Harris, federal courts are obligated to intervene when state processes are used purely for harassment."
        ),
        TimelineEvent(
            date = "Jan 16, 2026",
            title = "Nolle Prosequi & Strategic Mootness",
            description = "State prosecutors terminate the case via nolle prosequi. The petitioner argues this is a custom of 'strategic mootness' designed to systematically eviscerate federal civil rights review under Monell.",
            category = "Procedural",
            docIdRef = "petition_4",
            audioSnippetText = "In January 2026, the state entered a nolle prosequi, mooting the case before it could reach federal review.",
            branch = "Procedural/Monell",
            decisionLabel = "Does the systematic use of nolle prosequi shield municipal entities from civil rights liability?",
            decisionOutcome = "DECISION OUTCOME: Yes, creating a jurisdictional loop hole. Citing Southern Pacific Terminal, McLaughlin asks the Court to expand the 'capable of repetition, yet evading review' doctrine."
        ),
        TimelineEvent(
            date = "Mar 31, 2026",
            title = "Filing Consolidated Petitions in the Supreme Court",
            description = "Five comprehensive petitions are filed in the Supreme Court, proposing a unified constitutional theory linking advanced neurogenetics, administrative APA compliance, and post-quantum digital access.",
            category = "Constitutional",
            docIdRef = "petition_1",
            audioSnippetText = "On March 31, 2026, McLaughlin filed his consolidated petitions, introducing the concept of the Restraint of Silence to the Supreme Court.",
            branch = "Procedural/Monell",
            decisionLabel = "Is the systemic denial of cognitive aids a structural violation of due process?",
            decisionOutcome = "DECISION OUTCOME: Yes. Forcing a neurodivergent citizen to litigate without cognitive 'digital ramps' effectively denies them a meaningful opportunity to be heard, violating the Fifth Amendment."
        )
    )
}
