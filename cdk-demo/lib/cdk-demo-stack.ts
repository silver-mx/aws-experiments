import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as ec2 from 'aws-cdk-lib/aws-ec2';

export class CdkDemoStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Configure a VPC that expands to 2 AZs
    new ec2.Vpc(this, 'cdk-mainVPC', {
      maxAzs: 2,
      subnetConfiguration: [{
        cidrMask: 24,
        name: 'public-subnet',
        subnetType: ec2.SubnetType.PUBLIC,
      }]
    });

  }
}
