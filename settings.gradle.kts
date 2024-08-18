plugins {
    id("com.gradle.develocity") version "3.17.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "filthy-rich-clients"

includeBuild("build-logic")

include(
    "java-net:animated-transitions-swing",
    "AnimatedTransitions:ImageBrowser",
    "AnimatedTransitions:SearchTransition",
    "Animation:AnimatedGraphics",
    "Animation:FadingButton",
    "Animation:MovingButton",
    "Animation:MovingButtonContainer",
    "Animation:SwingTimerDemo",
    "Animation:TimeResolution",
    "Animation:UtilTimerDemo",
    "Composites:AlphaComposites",
    "Composites:BlendComposites",
    "Composites:SourceIn",
    "DynamicEffects:Bloom",
    "DynamicEffects:BloomOpenGL",
    "DynamicEffects:Fading",
    "DynamicEffects:Morphing",
    "DynamicEffects:Motion",
    "DynamicEffects:Pulse",
    "DynamicEffects:PulseField",
    "DynamicEffects:Spring",
    "GlassPane:GlassDragAndDrop",
    "GlassPane:GlassPanePainting",
    "GlassPane:InterceptEvents",
    "GlassPane:MouseCursor",
    "Gradients:MultiStopsGradient",
    "Gradients:RadialGradient",
    "Gradients:Reflection",
    "Gradients:TwoStopsGradient",
    "GraphicsFundamentals:AntiAliasingDemo",
    "GraphicsFundamentals:CopyAreaPerformance",
    "GraphicsFundamentals:DiagonalLineDemo",
    "GraphicsFundamentals:DrawShapes",
    "GraphicsFundamentals:FillDraw",
    "GraphicsFundamentals:FontHints",
    "GraphicsFundamentals:RotationAboutCenter",
    "GraphicsFundamentals:SimpleAttributes",
    "ImageProcessing:CustomImageOp",
    "ImageProcessing:ImageOps",
    "Images:PictureScaler",
    "Images:ScaleTest",
    "Images:ScalingMethods",
    "LayeredPanes:LayeredPaneLayout",
    "LayeredPanes:Layers",
    "LayeredPanes:StackLayout",
    "Performance:DataBufferGrabber",
    "Performance:IntermediateImages",
    "Performance:OptimalPrimitives",
    "RepaintManager:RepaintManager",
    "RepaintManager:TranslucentPanel",
    "SmoothMoves:ColorDifference",
    "SmoothMoves:SmoothMoves",
    "StaticEffects:Blur",
    "StaticEffects:BlurryReflection",
    "StaticEffects:BoxBlur",
    "StaticEffects:Brightness",
    "StaticEffects:DropShadow",
    "StaticEffects:FastBlur",
    "StaticEffects:GaussianBlur",
    "StaticEffects:SheddingLight",
    "StaticEffects:TextHighlighting",
    "StaticEffects:UnsharpMask",
    "SwingRenderingFundamentals:FreezeEDT",
    "SwingRenderingFundamentals:HighlightedButton",
    "SwingRenderingFundamentals:ImageLoader",
    "SwingRenderingFundamentals:OvalComponent",
    "SwingRenderingFundamentals:SafeRepaint",
    "SwingRenderingFundamentals:SwingThreading",
    "SwingRenderingFundamentals:SwingThreadingWait",
    "SwingRenderingFundamentals:TranslucentButton",
    "TimingFramework-Advanced:DiscreteInterpolation",
    "TimingFramework-Advanced:MultiStepRace",
    "TimingFramework-Advanced:MyIntAnim",
    "TimingFramework-Advanced:MyIntAnimPS",
    "TimingFramework-Advanced:SetterRace",
    "TimingFramework-Advanced:TriggerRace",
    "TimingFramework-Advanced:Triggers",
    "TimingFramework-Fundamentals:BasicRace",
    "TimingFramework-Fundamentals:FadingButtonTF",
    "TimingFramework-Fundamentals:NonLinearRace",
    "TimingFramework-Fundamentals:SplineEditor",
    "TimingFramework-Fundamentals:SplineInterpolatorTest",
)

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        // publishAlways()
        val isCI = providers.environmentVariable("CI").isPresent
        publishing.onlyIf { isCI }
        tag("CI")

        buildScanPublished {
            File("build-scan.txt").printWriter().use { writer ->
                writer.println(buildScanUri)
            }
        }

        if (providers.environmentVariable("GITHUB_ACTIONS").isPresent) {
            link("GitHub Repository", "https://github.com/" + System.getenv("GITHUB_REPOSITORY"))
            link(
                "GitHub Commit",
                "https://github.com/" + System.getenv("GITHUB_REPOSITORY") + "/commits/" + System.getenv("GITHUB_SHA")
            )


            listOf(
                "GITHUB_ACTION_REPOSITORY",
                "GITHUB_EVENT_NAME",
                "GITHUB_ACTOR",
                "GITHUB_BASE_REF",
                "GITHUB_HEAD_REF",
                "GITHUB_JOB",
                "GITHUB_REF",
                "GITHUB_REF_NAME",
                "GITHUB_REPOSITORY",
                "GITHUB_RUN_ID",
                "GITHUB_RUN_NUMBER",
                "GITHUB_SHA",
                "GITHUB_WORKFLOW"
            ).forEach { e ->
                val v = System.getenv(e)
                if (v != null) {
                    value(e, v)
                }
            }

            providers.environmentVariable("GITHUB_SERVER_URL").orNull?.let { ghUrl ->
                val ghRepo = System.getenv("GITHUB_REPOSITORY")
                val ghRunId = System.getenv("GITHUB_RUN_ID")
                link("Summary", "$ghUrl/$ghRepo/actions/runs/$ghRunId")
                link("PRs", "$ghUrl/$ghRepo/pulls")

                // see .github/workflows/build.yaml
                providers.environmentVariable("GITHUB_PR_NUMBER")
                    .orNull
                    .takeUnless { it.isNullOrBlank() }
                    .let { prNumber ->
                        link("PR", "$ghUrl/$ghRepo/pulls/$prNumber")
                    }
            }
        }
    }
}