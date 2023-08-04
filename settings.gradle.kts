import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

plugins {
    `gradle-enterprise`
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.6.0")
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

gradleEnterprise {
    if (providers.environmentVariable("CI").isPresent) {
        println("CI")
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
            publishAlways()
            tag("CI")
        }
    }
}