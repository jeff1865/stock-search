package com.onestore.ds;

import org.datavec.image.loader.CifarLoader;
import org.deeplearning4j.common.resources.DL4JResources;
import org.deeplearning4j.datasets.fetchers.DataSetType;
import org.deeplearning4j.datasets.iterator.impl.Cifar10DataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Created by a1000074 on 26/12/2019.
 */
public class CnnSample {

    private static int height = 32;
    private static int width = 32;
    private static int channels = 3;
    private static int numLabels = CifarLoader.NUM_LABELS;
    private static int batchSize = 10;
    private static long seed = 123L;
    private static int epochs = 4;

    public static void main(String ... v) {
        System.out.println("Active System ..");
        DL4JResources.setBaseDownloadURL("https://dl4jdata.blob.core.windows.net/");

        Cifar10DataSetIterator cifar = new Cifar10DataSetIterator(batchSize, new int[]{height, width}, DataSetType.TRAIN, null, seed);

        System.out.println("[dataSet] -> " + cifar.getLabels());


        MultiLayerNetwork network = new MultiLayerNetwork(createConf());
        network.init();

        network.fit(cifar);

        Evaluation evaluation = network.evaluate(new Cifar10DataSetIterator(batchSize, new int[]{height, width}, DataSetType.TEST, null, seed));
        System.out.println(evaluation.stats());

        System.out.println("Job Completed ..");
    }

    public static MultiLayerConfiguration createConf() {
        ConvolutionLayer layer0 = new ConvolutionLayer.Builder(5,5)
                .nIn(3)
                .nOut(16)
                .stride(1,1)
                .padding(2,2)
                .weightInit(WeightInit.XAVIER)
                .name("First convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer1 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2,2)
                .stride(2,2)
                .name("First subsampling layer")
                .build();

        ConvolutionLayer layer2 = new ConvolutionLayer.Builder(5,5)
                .nOut(20)
                .stride(1,1)
                .padding(2,2)
                .weightInit(WeightInit.XAVIER)
                .name("Second convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer3 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2,2)
                .stride(2,2)
                .name("Second subsampling layer")
                .build();

        ConvolutionLayer layer4 = new ConvolutionLayer.Builder(5,5)
                .nOut(20)
                .stride(1,1)
                .padding(2,2)
                .weightInit(WeightInit.XAVIER)
                .name("Third convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer5 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2,2)
                .stride(2,2)
                .name("Third subsampling layer")
                .build();

        OutputLayer layer6 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .weightInit(WeightInit.XAVIER)
                .name("Output")
                .nOut(10)
                .build();

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(12345)
//                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                .learningRate(0.001)
//                .regularization(true)
                .l2(0.0004)
                .updater(Updater.NESTEROVS)
//                .momentum(0.9)
                .list()
                .layer(0, layer0)
                .layer(1, layer1)
                .layer(2, layer2)
                .layer(3, layer3)
                .layer(4, layer4)
                .layer(5, layer5)
                .layer(6, layer6)
//                .pretrain(false)
//                .backprop(true)
                .setInputType(InputType.convolutional(32,32,3))
                .build();
        return configuration;
    }
}
